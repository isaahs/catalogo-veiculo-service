package br.com.fiap.sout.catalogo.application.usecases;

import br.com.fiap.sout.catalogo.application.ports.in.ListarVeiculoPort;
import br.com.fiap.sout.catalogo.application.ports.out.VeiculoRepositoryPort;
import br.com.fiap.sout.catalogo.domain.model.Veiculo;

import java.util.List;

public class ListarVeiculoUseCase implements ListarVeiculoPort {

    private final VeiculoRepositoryPort veiculoRepository;

    public ListarVeiculoUseCase(VeiculoRepositoryPort veiculoRepository) {
        this.veiculoRepository = veiculoRepository;
    }

    @Override
    public List<Veiculo> listarVeiculos() {
        return veiculoRepository.listarTodos();
    }
}
