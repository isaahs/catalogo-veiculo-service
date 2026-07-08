package br.com.fiap.sout.catalogo.adapter.in.web.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record VeiculoResponseDto(
        UUID id,
        String marca,
        String modelo,
        Integer ano,
        String cor,
        BigDecimal preco,
        String placa) {
}
