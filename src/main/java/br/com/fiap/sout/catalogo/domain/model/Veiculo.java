package br.com.fiap.sout.catalogo.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

public record Veiculo(UUID id,
                      String marca,
                      String modelo,
                      int ano,
                      String cor,
                      BigDecimal preco,
                      String placa) {
}
