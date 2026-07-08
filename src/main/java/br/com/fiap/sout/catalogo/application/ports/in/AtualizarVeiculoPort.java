package br.com.fiap.sout.catalogo.application.ports.in;

import br.com.fiap.sout.catalogo.domain.model.Veiculo;

public interface AtualizarVeiculoPort {

    Veiculo atualizarVeiculo(Veiculo veiculo);
}
