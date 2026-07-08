package br.com.fiap.sout.catalogo.adapter.in.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record VeiculoRequestDto(@NotBlank
                                String marca,
                                @NotBlank
                                String modelo,
                                @NotNull
                                Integer ano,
                                @NotBlank
                                String cor,
                                @NotNull
                                @Positive
                                BigDecimal preco,
                                @NotBlank
                                @Pattern(
                                        regexp = "^[A-Z]{3}-?[0-9][A-Z0-9][0-9]{2}$",
                                        message = "A placa deve seguir um formato brasileiro válido (ex: ABC1234, ABC-1234 ou ABC1D23)."
                                )
                                String placa) {

}
