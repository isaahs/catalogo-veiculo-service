package br.com.fiap.sout.catalogo.infra.config;

import br.com.fiap.sout.catalogo.application.ports.in.AtualizarVeiculoPort;
import br.com.fiap.sout.catalogo.application.ports.in.BuscarVeiculoPorIdPort;
import br.com.fiap.sout.catalogo.application.ports.in.CadastrarVeiculoPort;
import br.com.fiap.sout.catalogo.application.ports.in.ListarVeiculoPort;
import br.com.fiap.sout.catalogo.application.ports.out.SincronizarCatalogoPort;
import br.com.fiap.sout.catalogo.application.ports.out.VeiculoRepositoryPort;
import br.com.fiap.sout.catalogo.application.ports.out.VerificarVeiculoVendidoPort;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UseCaseConfigTest {

    private final VeiculoRepositoryPort veiculoRepositoryPort = mock(VeiculoRepositoryPort.class);
    private final SincronizarCatalogoPort sincronizarCatalogoPort = mock(SincronizarCatalogoPort.class);
    private final VerificarVeiculoVendidoPort verificarVeiculoVendidoPort = mock(VerificarVeiculoVendidoPort.class);
    private final UseCaseConfig config = new UseCaseConfig();

    @Test
    void deveInstanciarPorts() {
        BuscarVeiculoPorIdPort buscarPort = config.buscarVeiculoPorIdPort(veiculoRepositoryPort);
        assertNotNull(buscarPort);

        CadastrarVeiculoPort cadastrarPort = config.cadastrarVeiculoPort(veiculoRepositoryPort, sincronizarCatalogoPort);
        assertNotNull(cadastrarPort);

        ListarVeiculoPort listarPort = config.listarVeiculoPort(veiculoRepositoryPort);
        assertNotNull(listarPort);

        AtualizarVeiculoPort atualizarPort = config.atualizarVeiculoPort(veiculoRepositoryPort, sincronizarCatalogoPort, verificarVeiculoVendidoPort);
        assertNotNull(atualizarPort);
    }
}
