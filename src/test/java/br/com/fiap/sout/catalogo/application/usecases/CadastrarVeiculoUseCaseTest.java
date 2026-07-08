package br.com.fiap.sout.catalogo.application.usecases;

import br.com.fiap.sout.catalogo.application.ports.out.SincronizarCatalogoPort;
import br.com.fiap.sout.catalogo.application.ports.out.VeiculoRepositoryPort;
import br.com.fiap.sout.catalogo.domain.model.Veiculo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CadastrarVeiculoUseCaseTest {

    @Mock
    private VeiculoRepositoryPort veiculoRepository;

    @Mock
    private SincronizarCatalogoPort sincronizarCatalogo;

    private CadastrarVeiculoUseCase cadastrarVeiculoUseCase;

    @BeforeEach
    void setUp() {
        cadastrarVeiculoUseCase = new CadastrarVeiculoUseCase(veiculoRepository, sincronizarCatalogo);
    }

    @Test
    void deveCadastrarVeiculoComSucesso() {
        // Arrange
        Veiculo veiculoInput = new Veiculo(null, "Ford", "Fiesta", 2020, "Preto", new BigDecimal("50000.00"), "ABC1234");
        Veiculo veiculoSalvo = new Veiculo(UUID.randomUUID(), "Ford", "Fiesta", 2020, "Preto", new BigDecimal("50000.00"), "ABC1234");

        when(veiculoRepository.salvar(any(Veiculo.class))).thenReturn(veiculoSalvo);

        // Act
        Veiculo resultado = cadastrarVeiculoUseCase.cadastrar(veiculoInput);

        // Assert
        assertNotNull(resultado);
        assertNotNull(resultado.id());
        assertEquals(veiculoInput.marca(), resultado.marca());
        assertEquals(veiculoInput.modelo(), resultado.modelo());
        assertEquals(veiculoInput.ano(), resultado.ano());
        assertEquals(veiculoInput.cor(), resultado.cor());
        assertEquals(veiculoInput.preco(), resultado.preco());
        assertEquals(veiculoInput.placa(), resultado.placa());

        verify(veiculoRepository, times(1)).salvar(any(Veiculo.class));
        verify(sincronizarCatalogo, times(1)).sincronizarCatalogo(veiculoSalvo);
    }
}
