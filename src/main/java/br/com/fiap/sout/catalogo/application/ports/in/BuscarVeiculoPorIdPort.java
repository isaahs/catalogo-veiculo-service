package br.com.fiap.sout.catalogo.application.ports.in;

import br.com.fiap.sout.catalogo.domain.model.Veiculo;

import java.util.Optional;
import java.util.UUID;

public interface BuscarVeiculoPorIdPort {

    Veiculo buscarVeiculoPorId(UUID id);
}
