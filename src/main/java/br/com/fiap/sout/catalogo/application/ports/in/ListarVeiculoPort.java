package br.com.fiap.sout.catalogo.application.ports.in;

import br.com.fiap.sout.catalogo.domain.model.Veiculo;

import java.util.List;

public interface ListarVeiculoPort {

    List<Veiculo> listarVeiculos();
}
