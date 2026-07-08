package br.com.fiap.sout.catalogo.application.usecases;

import br.com.fiap.sout.catalogo.application.ports.in.AtualizarVeiculoPort;
import br.com.fiap.sout.catalogo.application.ports.out.SincronizarCatalogoPort;
import br.com.fiap.sout.catalogo.application.ports.out.VeiculoRepositoryPort;
import br.com.fiap.sout.catalogo.application.ports.out.VerificarVeiculoVendidoPort;
import br.com.fiap.sout.catalogo.domain.exceptions.PlacaAlteradaException;
import br.com.fiap.sout.catalogo.domain.exceptions.VeiculoNaoEncontradoException;
import br.com.fiap.sout.catalogo.domain.exceptions.VeiculoVendidoException;
import br.com.fiap.sout.catalogo.domain.model.Veiculo;

public class AtualizarVeiculoUseCase implements AtualizarVeiculoPort {

    private final VeiculoRepositoryPort veiculoRepository;
    private final SincronizarCatalogoPort sincronizar;
    private final VerificarVeiculoVendidoPort verificarVeiculoVendido;

    public AtualizarVeiculoUseCase(VeiculoRepositoryPort veiculoRepository, SincronizarCatalogoPort sincronizar, VerificarVeiculoVendidoPort verificarVeiculoVendido) {
        this.veiculoRepository = veiculoRepository;
        this.sincronizar = sincronizar;
        this.verificarVeiculoVendido = verificarVeiculoVendido;
    }

    @Override
    public Veiculo atualizarVeiculo(Veiculo veiculo) {

        var veiculoExistente = veiculoRepository.buscarPorId(veiculo.id())
                .orElseThrow(() -> new VeiculoNaoEncontradoException("Veículo não encontrado."));

        if (!veiculoExistente.placa().equals(veiculo.placa())) {
            throw new PlacaAlteradaException("A placa do veículo não pode ser alterada.");
        }

        if (verificarVeiculoVendido.verificarVeiculoVendido(veiculo.id())) {
            throw new VeiculoVendidoException("Veículo já vendido, não é possível atualizar.");
        }

        Veiculo veiculoAtualizado = veiculoRepository.salvar(veiculo);

        sincronizar.sincronizarCatalogo(veiculoAtualizado);
        return veiculoAtualizado;
    }
}
