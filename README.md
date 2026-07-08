# Catalogo Veiculo Service

Este é o **catalogo-service** (software principal), um microsserviço desenvolvido para o gerenciamento de catálogo de veículos em uma plataforma de revenda automotiva. Ele é responsável pelo cadastro e edição de dados de veículos, além de manter o serviço de vendas sincronizado.

---

## 🚀 Tecnologias Utilizadas

*   **Java 21**: Para uso de recursos modernos e records para imutabilidade.
*   **Spring Boot 4.1.0**: Framework base para a aplicação.
*   **Spring Data JPA**: Abstração de banco de dados.
*   **PostgreSQL**: Banco de dados relacional segregado.
*   **Liquibase**: Versionamento e evolução do esquema do banco de dados.
*   **Resilience4j**: Mecanismo de Retry para resiliência na comunicação de rede.
*   **Springdoc OpenAPI (Swagger)**: Documentação e teste interativo dos endpoints.
*   **MapStruct**: Mapeamento de dados entre entidades JPA e objetos de domínio/DTOs.

---

## 🏛️ Arquitetura e Decisões de Design

O projeto foi estruturado seguindo os princípios da **Arquitetura Hexagonal (Ports & Adapters)**, garantindo que o domínio esteja isolado de detalhes tecnológicos (frameworks, bancos de dados, integrações HTTP):

```
└─ src/main/java/br/com/fiap/sout/catalogo
   ├─ domain          <-- Domínio Puro (Regras de negócio, exceções, modelos)
   ├─ application     <-- Portas de Entrada/Saída e Casos de Uso
   ├─ adapter         <-- Adaptadores Web (Controllers) e Persistência/HTTP (Out)
   └─ infra           <-- Configurações gerais (Security, Schedulers, Beans)
```

### Premissas e Decisões Importantes
1.  **Imutabilidade no Domínio**: Os modelos de domínio são Java Records para garantir a imutabilidade e consistência dos estados durante as operações.
2.  **Segregação de Bancos de Dados**: Este microsserviço possui seu próprio banco de dados (`db_catalogo`), totalmente isolado do serviço de vendas (`vehicle-sales-service`), garantindo escalabilidade e independência.
3.  **Sincronização Resiliente**: A cada cadastro ou edição, o serviço envia os dados para o serviço de vendas via requisição HTTP. Caso ocorra uma falha de rede:
    *   O Resilience4j realiza **3 retentativas** com intervalo de 2 segundos.
    *   Se todas falharem, o status da sincronização é marcado como `ERRO_REDE` no banco de dados.
    *   Um scheduler (`VeiculoSyncScheduler`) executa a cada 5 minutos buscando pendências e reenviando-as.
4.  **Bloqueio de Edição**: Um veículo vendido não pode ter seus dados editados. O serviço de catálogo realiza uma verificação síncrona com o serviço de vendas (`/veiculos/{id}/vendido`) antes de permitir qualquer alteração.
5.  **Segurança M2M**: A comunicação de saída com o serviço de vendas é protegida por autenticação máquina-para-máquina (M2M) baseada em **JWT**.

### 🎯 Decisões de Escopo (Out of Scope)
Para manter o foco nos requisitos do desafio e evitar *scope creep*, as seguintes decisões de escopo foram tomadas de forma consciente:
*   **Status `INDISPONIVEL` (Laudo Cautelar Reprovado)**: Embora seja um conceito do domínio real de revenda de veículos (ex: veículo reprovado em vistoria), não foi solicitado no enunciado e não há regras ou gatilhos definidos para ele. Portanto, ficou de fora do escopo desta implementação.
*   **Placa obrigatória no cadastro**: Como o modelo de negócio é uma *revenda* (veículos usados/seminovos), assume-se a premissa de que todo veículo possui placa no momento do cadastro. Cenários de carros 0km sem emplacamento prévio estão fora de escopo.

---

## 🛠️ Como Executar Localmente

### Pré-requisitos
*   **Java 21** instalado.
*   **Docker** e **Docker Compose** instalados.

### Passo 1: Executar o Banco de Dados
Na raiz do projeto, inicie o container do PostgreSQL:
```bash
docker compose up -d
```

### Passo 2: Executar a Aplicação
Execute o comando Maven para iniciar o Spring Boot:
```bash
./mvnw spring-boot:run
```

A aplicação estará disponível em `http://localhost:8080`.

---

## ☸️ Kubernetes e Infraestrutura (IaC)

O projeto inclui arquivos de infraestrutura como código (IaC) e manifestos Kubernetes na pasta [terraform](file:///home/isadmot/Github/CatalogoService/terraform) e [k8s](file:///home/isadmot/Github/CatalogoService/k8s):

*   **Terraform (`terraform/main.tf`)**: Configurado para simular a criação de um cluster EKS e uma instância RDS PostgreSQL localmente usando o **Floci**. O arquivo utiliza uma variável de entrada sensitiva (`db_password`) para a senha do banco de dados, evitando segredos hardcoded no código de infraestrutura.
*   **Manifestos Kubernetes (`k8s/`)**:
    *   [secret.yaml](file:///home/isadmot/Github/CatalogoService/k8s/secret.yaml): Declara o recurso de `Secret` do Kubernetes contendo as credenciais codificadas em Base64 (`SPRING_DATASOURCE_PASSWORD` e `JWT_SECRET`).
    *   [deployment.yaml](file:///home/isadmot/Github/CatalogoService/k8s/deployment.yaml): Declara o deploy do microsserviço de catálogo, referenciando as variáveis sensíveis do `secret.yaml` usando `secretKeyRef` para evitar exposição de credenciais em texto puro.
    *   [service.yaml](file:///home/isadmot/Github/CatalogoService/k8s/service.yaml): Declara o serviço correspondente para expor o microsserviço de catálogo.
---

## 🧪 Como Executar os Testes

Para rodar todos os testes unitários da aplicação:
```bash
./mvnw test
```

### Testes Locais com o GitHub Actions (Act)
Como a pipeline de CI/CD está integrada com GitHub Actions, você pode rodar os workflows localmente usando o [Act](https://github.com/nektos/act):
```bash
act -s GITHUB_TOKEN=seu_token_aqui
```

---

## 📖 Documentação da API (OpenAPI/Swagger)

Com a aplicação rodando, você pode acessar:
*   **Interface Gráfica (Swagger UI)**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
*   **Especificação JSON**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)
