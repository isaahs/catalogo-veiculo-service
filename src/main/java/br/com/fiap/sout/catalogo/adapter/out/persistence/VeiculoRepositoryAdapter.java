package br.com.fiap.sout.catalogo.adapter.out.persistence;

import br.com.fiap.sout.catalogo.adapter.out.persistence.enums.StatusSincronizacao;
import br.com.fiap.sout.catalogo.adapter.out.persistence.mapper.VeiculoEntityMapper;
import br.com.fiap.sout.catalogo.adapter.out.persistence.repository.VeiculoJpaRepository;
import br.com.fiap.sout.catalogo.application.ports.out.VeiculoRepositoryPort;
import br.com.fiap.sout.catalogo.domain.model.Veiculo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Repository
public class VeiculoRepositoryAdapter implements VeiculoRepositoryPort {

    private final VeiculoJpaRepository veiculoJpa;
    private final VeiculoEntityMapper veiculoMapper;

    @Override
    public Veiculo salvar(Veiculo veiculo) {
        var entidadeMapeada = veiculoMapper.toVeiculoEntity(veiculo);

        var entidadeSalva = veiculoJpa.save(entidadeMapeada);

        return veiculoMapper.toDomain(entidadeSalva);
    }

    @Override
    public Optional<Veiculo> buscarPorId(UUID id) {
        return veiculoJpa.findById(id)
                .map(veiculoMapper::toDomain);
    }

    @Override
    public List<Veiculo> listarTodos() {
        return veiculoJpa.findAll()
                .stream()
                .map(veiculoMapper::toDomain)
                .toList();
    }

    @Override
    public List<Veiculo> buscarNaoSincronizados() {
        var statusParaBuscar = List.of(StatusSincronizacao.PENDENTE, StatusSincronizacao.ERRO_REDE);
        return veiculoJpa.findByStatusSincronizacaoIn(statusParaBuscar)
                .stream()
                .map(veiculoMapper::toDomain)
                .toList();
    }

    @Override
    public void marcarComoSincronizado(UUID id) {
        veiculoJpa.findById(id).ifPresent(entidade -> {
            entidade.setStatusSincronizacao(StatusSincronizacao.SINCRONIZADO);
            veiculoJpa.save(entidade);
        });
    }

    @Override
    public void marcarComoFalhaSincronizacao(UUID id) {
        veiculoJpa.findById(id).ifPresent(entidade -> {
            entidade.setStatusSincronizacao(StatusSincronizacao.ERRO_REDE);
            veiculoJpa.save(entidade);
        });
    }
}
