package br.com.fiap.sout.catalogo.application.ports.out;

import java.util.UUID;

public interface VerificarVeiculoVendidoPort {

    boolean verificarVeiculoVendido(UUID id);
}
