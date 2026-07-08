package br.com.fiap.sout.catalogo.application.ports.out;

import br.com.fiap.sout.catalogo.domain.model.Veiculo;

public interface SincronizarCatalogoPort {

    void sincronizarCatalogo(Veiculo veiculo);
}
