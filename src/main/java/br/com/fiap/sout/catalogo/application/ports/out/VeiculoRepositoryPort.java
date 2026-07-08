package br.com.fiap.sout.catalogo.application.ports.out;

import br.com.fiap.sout.catalogo.adapter.out.persistence.entity.VeiculoEntity;
import br.com.fiap.sout.catalogo.adapter.out.persistence.enums.StatusSincronizacao;
import br.com.fiap.sout.catalogo.domain.model.Veiculo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VeiculoRepositoryPort {

    Veiculo salvar(Veiculo veiculo);

    Optional<Veiculo> buscarPorId(UUID id);

    List<Veiculo> listarTodos();

    List<Veiculo> buscarNaoSincronizados();

    void marcarComoSincronizado(UUID id);

    void marcarComoFalhaSincronizacao(UUID id);

}
