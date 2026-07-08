package br.com.fiap.sout.catalogo.application.usecases;

import br.com.fiap.sout.catalogo.application.ports.out.VeiculoRepositoryPort;
import br.com.fiap.sout.catalogo.domain.model.Veiculo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListarVeiculoUseCaseTest {

    @Mock
    private VeiculoRepositoryPort veiculoRepository;

    private ListarVeiculoUseCase listarVeiculoUseCase;

    @BeforeEach
    void setUp() {
        listarVeiculoUseCase = new ListarVeiculoUseCase(veiculoRepository);
    }

    @Test
    void deveListarVeiculosComSucesso() {
        // Arrange
        Veiculo v1 = new Veiculo(UUID.randomUUID(), "Fiat", "Uno", 2018, "Branco", new BigDecimal("30000.00"), "XYZ9876");
        Veiculo v2 = new Veiculo(UUID.randomUUID(), "Ford", "Fiesta", 2020, "Preto", new BigDecimal("50000.00"), "ABC1234");
        when(veiculoRepository.listarTodos()).thenReturn(List.of(v1, v2));

        // Act
        List<Veiculo> resultado = listarVeiculoUseCase.listarVeiculos();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertTrue(resultado.contains(v1));
        assertTrue(resultado.contains(v2));
        verify(veiculoRepository, times(1)).listarTodos();
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHouverVeiculos() {
        // Arrange
        when(veiculoRepository.listarTodos()).thenReturn(List.of());

        // Act
        List<Veiculo> resultado = listarVeiculoUseCase.listarVeiculos();

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(veiculoRepository, times(1)).listarTodos();
    }
}
