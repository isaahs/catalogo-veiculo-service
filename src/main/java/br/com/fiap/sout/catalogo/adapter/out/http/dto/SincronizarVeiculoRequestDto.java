package br.com.fiap.sout.catalogo.adapter.out.http.dto;

import br.com.fiap.sout.catalogo.domain.model.Veiculo;
import java.math.BigDecimal;
import java.util.UUID;

public record SincronizarVeiculoRequestDto(
    UUID veiculoId,
    String marca,
    String modelo,
    int ano,
    String cor,
    BigDecimal preco,
    String placa
) {
    public static SincronizarVeiculoRequestDto from(Veiculo veiculo) {
        return new SincronizarVeiculoRequestDto(
            veiculo.id(),
            veiculo.marca(),
            veiculo.modelo(),
            veiculo.ano(),
            veiculo.cor(),
            veiculo.preco(),
            veiculo.placa()
        );
    }
}
