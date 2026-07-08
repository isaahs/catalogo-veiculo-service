package br.com.fiap.sout.catalogo.application.usecases;

import br.com.fiap.sout.catalogo.application.ports.in.BuscarVeiculoPorIdPort;
import br.com.fiap.sout.catalogo.application.ports.out.VeiculoRepositoryPort;
import br.com.fiap.sout.catalogo.domain.model.Veiculo;

import br.com.fiap.sout.catalogo.domain.exceptions.VeiculoNaoEncontradoException;
import java.util.Optional;
import java.util.UUID;

public class BuscarVeiculoPorIdUseCase implements BuscarVeiculoPorIdPort {

    private final VeiculoRepositoryPort veiculoRepository;

    public BuscarVeiculoPorIdUseCase(VeiculoRepositoryPort veiculoRepository) {
        this.veiculoRepository = veiculoRepository;
    }

    @Override
    public Veiculo buscarVeiculoPorId(UUID id) {
        return veiculoRepository.buscarPorId(id)
                .orElseThrow(() -> new VeiculoNaoEncontradoException("Veículo não encontrado."));
    }
}
