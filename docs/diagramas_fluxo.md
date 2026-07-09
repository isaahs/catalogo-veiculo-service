# Diagramas de Fluxo — Plataforma de Revenda de Veículos

Este documento descreve detalhadamente os fluxos de integração, sincronização de dados e processos de negócio entre os microsserviços `vehicle-catalog-service` (software principal) e `vehicle-sales-service` (serviço de vendas).

---

## 1. Cadastro, Edição e Sincronização de Veículos

Quando um veículo é cadastrado ou editado no software principal, os dados devem ser sincronizados com o serviço de vendas (réplica local). Caso ocorra uma falha de rede/integração, o sistema utiliza uma estratégia de resiliência e retentativa assíncrona.

### Diagrama de Sequência (Sincronização Inicial)

```mermaid
sequenceDiagram
    autonumber
    actor User as Usuário / Operador
    participant Catalog as vehicle-catalog-service
    participant DB_Catalog as DB Principal (Catalog)
    participant Sales as vehicle-sales-service
    participant DB_Sales as DB Vendas (Sales)

    User->>Catalog: POST /veiculos (ou PUT /veiculos/{id})
    Catalog->>DB_Catalog: Salva/Edita Veículo (StatusSincronizacao = PENDENTE)
    Catalog->>Sales: HTTP POST /veiculos (Sincronização)
    
    alt Sincronização com Sucesso
        Sales->>DB_Sales: Salva/Edita ItemCatalogo (status = DISPONIVEL)
        Sales-->>Catalog: 201 Created / 200 OK
        Catalog->>DB_Catalog: Atualiza StatusSincronizacao = SINCRONIZADO
        Catalog-->>User: Sucesso (Veículo cadastrado/editado)
    else Falha de Conexão / Timeout (Resilience4j)
        Catalog->>DB_Catalog: Atualiza StatusSincronizacao = ERRO_REDE
        Catalog-->>User: Sucesso (Salvo localmente, sincronização agendada)
    end
```

### Diagrama de Processo (Scheduler de Retentativa)

```mermaid
flowchart TD
    Start([Início do Scheduler - Cada 5 min]) --> Fetch[Buscar Veículos com StatusSincronizacao = ERRO_REDE]
    Fetch --> Loop{Tem veículos pendentes?}
    Loop -- Não --> End([Fim])
    Loop -- Sim --> Sync[Enviar HTTP POST/PUT para vehicle-sales-service]
    Sync --> Success{Sucesso?}
    Success -- Sim --> UpdateSuccess[Atualizar StatusSincronizacao = SINCRONIZADO no DB Principal]
    Success -- Não --> KeepError[Manter StatusSincronizacao = ERRO_REDE]
    UpdateSuccess --> Loop
    KeepError --> Loop
```

### Componentes Envolvidos
- **Controller/Adapter de Entrada**: [VeiculoAdapter](file:///home/isadmot/Github/CatalogoService/src/main/java/br/com/fiap/sout/catalogo/adapter/in/web/VeiculoAdapter.java)
- **Use Cases**: [CadastrarVeiculoUseCase](file:///home/isadmot/Github/CatalogoService/src/main/java/br/com/fiap/sout/catalogo/application/usecases/CadastrarVeiculoUseCase.java) e [AtualizarVeiculoUseCase](file:///home/isadmot/Github/CatalogoService/src/main/java/br/com/fiap/sout/catalogo/application/usecases/AtualizarVeiculoUseCase.java)
- **Porta de Saída (Sync)**: [SincronizarCatalogoPort](file:///home/isadmot/Github/CatalogoService/src/main/java/br/com/fiap/sout/catalogo/application/ports/out/SincronizarCatalogoPort.java)
- **Adapter de Integração HTTP**: [VendasVeiculoAdapterHttp](file:///home/isadmot/Github/CatalogoService/src/main/java/br/com/fiap/sout/catalogo/adapter/out/http/VendasVeiculoAdapterHttp.java)
- **Scheduler**: [VeiculoSyncScheduler](file:///home/isadmot/Github/CatalogoService/src/main/java/br/com/fiap/sout/catalogo/infra/scheduler/VeiculoSyncScheduler.java)
- **Enum de Status**: [StatusSincronizacao](file:///home/isadmot/Github/CatalogoService/src/main/java/br/com/fiap/sout/catalogo/adapter/out/persistence/enums/StatusSincronizacao.java)

---

## 2. Edição de Veículo (Validação e Bloqueio de Veículo Vendido)

Para garantir a consistência do negócio, a edição de um veículo passa por verificações rígidas de existência, imutabilidade da placa e verificação se o veículo já foi vendido.

### Diagrama de Sequência

```mermaid
sequenceDiagram
    autonumber
    actor User as Usuário / Operador
    participant Catalog as vehicle-catalog-service
    participant DB_Catalog as DB Principal (Catalog)
    participant Sales as vehicle-sales-service

    User->>Catalog: PUT /veiculos/{id} (Edição)
    Catalog->>DB_Catalog: Busca veículo por ID
    
    alt 1. Veículo não encontrado
        DB_Catalog-->>Catalog: Vazio
        Catalog-->>User: HTTP 404 Not Found
    else 2. Veículo encontrado
        Catalog->>Catalog: Valida placa (veiculoExistente.placa == veiculoRequest.placa)
        alt Placa foi alterada
            Catalog-->>User: HTTP 409 Conflict (A placa não pode ser alterada)
        else Placa idêntica
            Catalog->>Sales: HTTP GET /veiculos/{id}/vendido (Verificação de venda)
            Sales-->>Catalog: HTTP 200 OK (sold: true / false)
            
            alt 3. Veículo já vendido (sold = true)
                Catalog-->>User: HTTP 409 Conflict (Veículo já vendido, não é possível atualizar)
            else 4. Veículo disponível (sold = false)
                Catalog->>DB_Catalog: Salva veículo atualizado (StatusSincronizacao = PENDENTE)
                Catalog->>Sales: HTTP POST /veiculos (Sincronização)
                Note over Catalog,Sales: Segue o fluxo de sincronização e retentativas se necessário.
                Catalog-->>User: HTTP 200 OK (Veículo atualizado com sucesso)
            end
        end
    end
```

### Componentes Envolvidos
- **Porta de Saída (Check)**: [VerificarVeiculoVendidoPort](file:///home/isadmot/Github/CatalogoService/src/main/java/br/com/fiap/sout/catalogo/application/ports/out/VerificarVeiculoVendidoPort.java)
- **Adapter de Integração HTTP**: [VendasVeiculoAdapterHttp](file:///home/isadmot/Github/CatalogoService/src/main/java/br/com/fiap/sout/catalogo/adapter/out/http/VendasVeiculoAdapterHttp.java)
- **Use Case**: [AtualizarVeiculoUseCase](file:///home/isadmot/Github/CatalogoService/src/main/java/br/com/fiap/sout/catalogo/application/usecases/AtualizarVeiculoUseCase.java)
- **Exceções de Domínio**: 
  - [VeiculoNaoEncontradoException](file:///home/isadmot/Github/CatalogoService/src/main/java/br/com/fiap/sout/catalogo/domain/exceptions/VeiculoNaoEncontradoException.java) (HTTP 404)
  - [PlacaAlteradaException](file:///home/isadmot/Github/CatalogoService/src/main/java/br/com/fiap/sout/catalogo/domain/exceptions/PlacaAlteradaException.java) (HTTP 409)
  - [VeiculoVendidoException](file:///home/isadmot/Github/CatalogoService/src/main/java/br/com/fiap/sout/catalogo/domain/exceptions/VeiculoVendidoException.java) (HTTP 409)

---

## 3. Fluxo de Compra e Reserva do Veículo

Sob pico de tráfego, múltiplos clientes podem tentar comprar o mesmo veículo ao mesmo tempo. Para evitar concorrência nociva, é feito um update condicional atômico na base de dados de vendas.

### Diagrama de Sequência

```mermaid
sequenceDiagram
    autonumber
    actor Buyer as Comprador / Cliente
    participant Sales as vehicle-sales-service
    participant DB_Sales as DB Vendas (Sales)

    Buyer->>Sales: POST /vendas (Efetuar Compra)
    Note over Sales, DB_Sales: Executa Query Condicional de Atualização
    Sales->>DB_Sales: UPDATE item_catalogo SET status = 'RESERVADO' WHERE id = ? AND status = 'DISPONIVEL'
    
    alt 0 linhas afetadas (Veículo já reservado ou vendido)
        Sales-->>Buyer: HTTP 409 Conflict (Veículo indisponível)
    else 1 linha afetada (Sucesso na reserva)
        Sales->>DB_Sales: Cria Venda (status = PENDENTE_PAGAMENTO, expiraEm = NOW + X min)
        Sales-->>Buyer: HTTP 201 Created (Venda iniciada com código de pagamento)
    end
```

### Componentes Envolvidos
- **Concorrência**: Implementado via query com `@Modifying` e `@Query` no repositório de vendas:
  `UPDATE ItemCatalogoEntity i SET i.status = 'RESERVADO' WHERE i.id = :id AND i.status = 'DISPONIVEL'`
- **Controller/Adapter de Entrada**: `VendaAdapter` (Sales Service)
- **Use Case**: `EfetuarVendaUseCase` (Sales Service)

---

## 4. Webhook de Pagamento (Idempotência e Segurança)

O webhook é o endpoint chamado pela processadora de pagamentos externa. O fluxo deve validar a assinatura HMAC-SHA256 para garantir autenticidade e processar o pagamento de forma idempotente.

### Diagrama de Sequência

```mermaid
sequenceDiagram
    autonumber
    participant Gateway as Processador de Pagamento
    participant Sales as vehicle-sales-service
    participant DB_Sales as DB Vendas (Sales)

    Gateway->>Sales: POST /pagamentos/webhook (HMAC-SHA256 Signature)
    Note over Sales: Filtro de segurança valida a assinatura com segredo compartilhado
    
    alt Assinatura ausente ou malformada
        Sales-->>Gateway: HTTP 400 Bad Request
    else Assinatura incorreta (verificação falhou)
        Sales-->>Gateway: HTTP 403 Forbidden
    else Assinatura válida
        Note over Sales, DB_Sales: Atualização Condicional Idempotente
        Sales->>DB_Sales: UPDATE venda SET status = ? WHERE codigo_pagamento = ? AND status = 'PENDENTE_PAGAMENTO'
        alt 0 linhas afetadas (Webhook duplicado / Venda já processada)
            Sales-->>Gateway: HTTP 200 OK (Sem alterações - Idempotente)
        else 1 linha afetada (Primeiro processamento)
            alt Pagamento APROVADO
                Sales->>DB_Sales: Atualiza Venda para CONFIRMADA
                Sales->>DB_Sales: UPDATE item_catalogo SET status = 'VENDIDO' WHERE id = ?
            else Pagamento CANCELADO
                Sales->>DB_Sales: Atualiza Venda para CANCELADA
                Sales->>DB_Sales: UPDATE item_catalogo SET status = 'DISPONIVEL' WHERE id = ?
            end
            Sales-->>Gateway: HTTP 204 No Content / 200 OK
        end
    end
```

### Componentes Envolvidos
- **Segurança**: Filtro HTTP dedicado no `vehicle-sales-service` que calcula e valida o HMAC-SHA256 do corpo da requisição.
- **Idempotência**: Garantida pela restrição `WHERE status = 'PENDENTE_PAGAMENTO'` na query de atualização da venda.

---

## 5. Expiração de Reserva Órfã (Clean Up)

Se o comprador iniciar uma venda, o veículo for reservado, mas o webhook de pagamento nunca chegar (ou o cliente desistir), a reserva ficará órfã. Um scheduler monitora e limpa essas reservas expiradas.

### Diagrama de Processo

```mermaid
flowchart TD
    Start([Início do Scheduler - Expirar Reservas]) --> Fetch[Buscar Vendas com status = PENDENTE_PAGAMENTO e expiraEm < NOW]
    Fetch --> Loop{Tem reservas expiradas?}
    Loop -- Não --> End([Fim])
    Loop -- Sim --> CancelSale[Atualizar Venda para CANCELADA no DB]
    CancelSale --> ReleaseItem[UPDATE item_catalogo SET status = 'DISPONIVEL' WHERE id = item_catalogo_id]
    ReleaseItem --> Loop
```

### Componentes Envolvidos
- **Scheduler**: `@Scheduled` no `vehicle-sales-service`.
- **Use Case**: `ExpirarReservasUseCase` (Sales Service).
