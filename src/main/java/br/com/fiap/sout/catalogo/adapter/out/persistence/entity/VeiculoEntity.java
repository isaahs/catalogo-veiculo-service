package br.com.fiap.sout.catalogo.adapter.out.persistence.entity;

import br.com.fiap.sout.catalogo.adapter.out.persistence.enums.StatusSincronizacao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "tb_veiculos")
public class VeiculoEntity {

    @Id
    private UUID id;
    private String marca;
    private String modelo;
    private int ano;
    private String cor;
    private BigDecimal preco;
    @Enumerated(EnumType.STRING)
    @Column(name = "status_sincronizacao", nullable = false)
    private StatusSincronizacao statusSincronizacao = StatusSincronizacao.PENDENTE;
    @Column(unique = true, nullable = false)
    private String placa;

}
