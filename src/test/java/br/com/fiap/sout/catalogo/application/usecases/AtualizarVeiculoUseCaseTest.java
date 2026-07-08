package br.com.fiap.sout.catalogo.application.usecases;

import br.com.fiap.sout.catalogo.application.ports.out.SincronizarCatalogoPort;
import br.com.fiap.sout.catalogo.application.ports.out.VeiculoRepositoryPort;
import br.com.fiap.sout.catalogo.application.ports.out.VerificarVeiculoVendidoPort;
import br.com.fiap.sout.catalogo.domain.exceptions.PlacaAlteradaException;
import br.com.fiap.sout.catalogo.domain.exceptions.VeiculoNaoEncontradoException;
import br.com.fiap.sout.catalogo.domain.exceptions.VeiculoVendidoException;
import br.com.fiap.sout.catalogo.domain.model.Veiculo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AtualizarVeiculoUseCaseTest {

    @Mock
    private VeiculoRepositoryPort veiculoRepository;

    @Mock
    private SincronizarCatalogoPort sincronizarCatalogo;

    @Mock
    private VerificarVeiculoVendidoPort verificarVeiculoVendido;

    private AtualizarVeiculoUseCase atualizarVeiculoUseCase;

    @BeforeEach
    void setUp() {
        atualizarVeiculoUseCase = new AtualizarVeiculoUseCase(veiculoRepository, sincronizarCatalogo, verificarVeiculoVendido);
    }

    @Test
    void deveAtualizarVeiculoComSucesso() {
        // Arrange
        UUID id = UUID.randomUUID();
        Veiculo veiculoExistente = new Veiculo(id, "Fiat", "Uno", 2018, "Branco", new BigDecimal("30000.00"), "XYZ9876");
        Veiculo veiculoUpdate = new Veiculo(id, "Fiat", "Uno Evolution", 2018, "Vermelho", new BigDecimal("32000.00"), "XYZ9876");

        when(veiculoRepository.buscarPorId(id)).thenReturn(Optional.of(veiculoExistente));
        when(verificarVeiculoVendido.verificarVeiculoVendido(id)).thenReturn(false);
        when(veiculoRepository.salvar(any(Veiculo.class))).thenReturn(veiculoUpdate);

        // Act
        Veiculo resultado = atualizarVeiculoUseCase.atualizarVeiculo(veiculoUpdate);

        // Assert
        assertNotNull(resultado);
        assertEquals(veiculoUpdate.modelo(), resultado.modelo());
        assertEquals(veiculoUpdate.cor(), resultado.cor());
        assertEquals(veiculoUpdate.preco(), resultado.preco());
        assertEquals(veiculoExistente.placa(), resultado.placa()); // Placa deve se manter igual

        verify(veiculoRepository, times(1)).buscarPorId(id);
        verify(verificarVeiculoVendido, times(1)).verificarVeiculoVendido(id);
        verify(veiculoRepository, times(1)).salvar(veiculoUpdate);
        verify(sincronizarCatalogo, times(1)).sincronizarCatalogo(veiculoUpdate);
    }

    @Test
    void deveLancarExcecaoQuandoVeiculoNaoExistir() {
        // Arrange
        UUID id = UUID.randomUUID();
        Veiculo veiculoUpdate = new Veiculo(id, "Fiat", "Uno", 2018, "Branco", new BigDecimal("30000.00"), "XYZ9876");

        when(veiculoRepository.buscarPorId(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(VeiculoNaoEncontradoException.class, () -> atualizarVeiculoUseCase.atualizarVeiculo(veiculoUpdate));

        verify(veiculoRepository, times(1)).buscarPorId(id);
        verify(veiculoRepository, never()).salvar(any());
        verify(sincronizarCatalogo, never()).sincronizarCatalogo(any());
    }

    @Test
    void deveLancarExcecaoQuandoTentarAlterarPlaca() {
        // Arrange
        UUID id = UUID.randomUUID();
        Veiculo veiculoExistente = new Veiculo(id, "Fiat", "Uno", 2018, "Branco", new BigDecimal("30000.00"), "XYZ9876");
        Veiculo veiculoUpdate = new Veiculo(id, "Fiat", "Uno", 2018, "Branco", new BigDecimal("30000.00"), "ABC1234"); // Placa alterada

        when(veiculoRepository.buscarPorId(id)).thenReturn(Optional.of(veiculoExistente));

        // Act & Assert
        assertThrows(PlacaAlteradaException.class, () -> atualizarVeiculoUseCase.atualizarVeiculo(veiculoUpdate));

        verify(veiculoRepository, times(1)).buscarPorId(id);
        verify(veiculoRepository, never()).salvar(any());
        verify(sincronizarCatalogo, never()).sincronizarCatalogo(any());
    }

    @Test
    void deveLancarExcecaoQuandoVeiculoJaEstiverVendido() {
        // Arrange
        UUID id = UUID.randomUUID();
        Veiculo veiculoExistente = new Veiculo(id, "Fiat", "Uno", 2018, "Branco", new BigDecimal("30000.00"), "XYZ9876");
        Veiculo veiculoUpdate = new Veiculo(id, "Fiat", "Uno", 2018, "Branco", new BigDecimal("30000.00"), "XYZ9876");

        when(veiculoRepository.buscarPorId(id)).thenReturn(Optional.of(veiculoExistente));
        when(verificarVeiculoVendido.verificarVeiculoVendido(id)).thenReturn(true); // Retorna que ja foi vendido

        // Act & Assert
        assertThrows(VeiculoVendidoException.class, () -> atualizarVeiculoUseCase.atualizarVeiculo(veiculoUpdate));

        verify(veiculoRepository, times(1)).buscarPorId(id);
        verify(verificarVeiculoVendido, times(1)).verificarVeiculoVendido(id);
        verify(veiculoRepository, never()).salvar(any());
        verify(sincronizarCatalogo, never()).sincronizarCatalogo(any());
    }
}
