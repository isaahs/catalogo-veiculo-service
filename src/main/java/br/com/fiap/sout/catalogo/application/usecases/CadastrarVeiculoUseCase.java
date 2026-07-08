package br.com.fiap.sout.catalogo.application.usecases;

import br.com.fiap.sout.catalogo.application.ports.in.CadastrarVeiculoPort;
import br.com.fiap.sout.catalogo.application.ports.out.SincronizarCatalogoPort;
import br.com.fiap.sout.catalogo.application.ports.out.VeiculoRepositoryPort;
import br.com.fiap.sout.catalogo.domain.model.Veiculo;

import java.util.UUID;

public class CadastrarVeiculoUseCase implements CadastrarVeiculoPort {

    private final VeiculoRepositoryPort veiculoRepository;

    private final SincronizarCatalogoPort sincronizar;

    public CadastrarVeiculoUseCase(VeiculoRepositoryPort veiculoRepository, SincronizarCatalogoPort sincronizarCatalogo) {
        this.veiculoRepository = veiculoRepository;
        this.sincronizar = sincronizarCatalogo;
    }

    @Override
    public Veiculo cadastrar(Veiculo veiculo) {
        Veiculo veiculoId = new Veiculo(UUID.randomUUID(),
                veiculo.marca(),
                veiculo.modelo(),
                veiculo.ano(),
                veiculo.cor(),
                veiculo.preco(),
                veiculo.placa());

        Veiculo veiculoCadastrado = veiculoRepository.salvar(veiculoId);
        sincronizar.sincronizarCatalogo(veiculoCadastrado);
        return veiculoCadastrado;
    }
}
