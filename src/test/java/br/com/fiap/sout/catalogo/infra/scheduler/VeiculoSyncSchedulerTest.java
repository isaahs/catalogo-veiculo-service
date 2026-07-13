package br.com.fiap.sout.catalogo.infra.scheduler;

import br.com.fiap.sout.catalogo.application.ports.out.SincronizarCatalogoPort;
import br.com.fiap.sout.catalogo.application.ports.out.VeiculoRepositoryPort;
import br.com.fiap.sout.catalogo.domain.model.Veiculo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VeiculoSyncSchedulerTest {

    @Mock
    private VeiculoRepositoryPort veiculoRepositoryPort;

    @Mock
    private SincronizarCatalogoPort sincronizarCatalogoPort;

    @InjectMocks
    private VeiculoSyncScheduler veiculoSyncScheduler;

    @Test
    void deveSincronizarVeiculosPendentes() {
        // Arrange
        Veiculo veiculo1 = new Veiculo(UUID.randomUUID(), "Ford", "Ka", 2019, "Prata", new BigDecimal("40000.00"), "ABC1234");
        Veiculo veiculo2 = new Veiculo(UUID.randomUUID(), "Chevrolet", "Onix", 2020, "Preto", new BigDecimal("50000.00"), "XYZ9876");

        when(veiculoRepositoryPort.buscarNaoSincronizados()).thenReturn(List.of(veiculo1, veiculo2));

        // Act
        ReflectionTestUtils.invokeMethod(veiculoSyncScheduler, "sincronizarVeiculos");

        // Assert
        verify(veiculoRepositoryPort, times(1)).buscarNaoSincronizados();
        verify(sincronizarCatalogoPort, times(1)).sincronizarCatalogo(veiculo1);
        verify(sincronizarCatalogoPort, times(1)).sincronizarCatalogo(veiculo2);
    }
}
