package br.com.fiap.sout.catalogo.application.usecases;

import br.com.fiap.sout.catalogo.application.ports.out.VeiculoRepositoryPort;
import br.com.fiap.sout.catalogo.domain.model.Veiculo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.fiap.sout.catalogo.domain.exceptions.VeiculoNaoEncontradoException;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BuscarVeiculoPorIdUseCaseTest {

    @Mock
    private VeiculoRepositoryPort veiculoRepository;

    private BuscarVeiculoPorIdUseCase buscarVeiculoPorIdUseCase;

    @BeforeEach
    void setUp() {
        buscarVeiculoPorIdUseCase = new BuscarVeiculoPorIdUseCase(veiculoRepository);
    }

    @Test
    void deveBuscarVeiculoPorIdComSucesso() {
        // Arrange
        UUID id = UUID.randomUUID();
        Veiculo veiculo = new Veiculo(id, "Fiat", "Uno", 2018, "Branco", new BigDecimal("30000.00"), "XYZ9876");
        when(veiculoRepository.buscarPorId(id)).thenReturn(Optional.of(veiculo));

        // Act
        Veiculo resultado = buscarVeiculoPorIdUseCase.buscarVeiculoPorId(id);

        // Assert
        assertNotNull(resultado);
        assertEquals(veiculo, resultado);
        verify(veiculoRepository, times(1)).buscarPorId(id);
    }

    @Test
    void deveLancarExceptionQuandoVeiculoNaoEncontrado() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(veiculoRepository.buscarPorId(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(VeiculoNaoEncontradoException.class, () -> buscarVeiculoPorIdUseCase.buscarVeiculoPorId(id));
        verify(veiculoRepository, times(1)).buscarPorId(id);
    }
}
