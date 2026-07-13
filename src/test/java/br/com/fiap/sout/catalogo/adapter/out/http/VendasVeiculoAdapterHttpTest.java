package br.com.fiap.sout.catalogo.adapter.out.http;

import br.com.fiap.sout.catalogo.adapter.out.http.dto.SincronizarVeiculoRequestDto;
import br.com.fiap.sout.catalogo.application.ports.out.VeiculoRepositoryPort;
import br.com.fiap.sout.catalogo.domain.exceptions.ServicoVendasIndisponivelException;
import br.com.fiap.sout.catalogo.domain.model.Veiculo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VendasVeiculoAdapterHttpTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private VeiculoRepositoryPort veiculoRepository;

    @InjectMocks
    private VendasVeiculoAdapterHttp vendasVeiculoAdapterHttp;

    private static final String VENDAS_SERVICE_URL = "http://localhost:8081";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(vendasVeiculoAdapterHttp, "vendasServiceUrl", VENDAS_SERVICE_URL);
    }

    @Test
    void deveSincronizarCatalogoComSucesso() {
        // Arrange
        UUID id = UUID.randomUUID();
        Veiculo veiculo = new Veiculo(id, "Honda", "Civic", 2021, "Cinza", new BigDecimal("120000.00"), "ABC1D23");
        String url = VENDAS_SERVICE_URL + "/veiculos";

        when(restTemplate.postForEntity(eq(url), any(SincronizarVeiculoRequestDto.class), eq(Void.class)))
                .thenReturn(null);

        // Act
        vendasVeiculoAdapterHttp.sincronizarCatalogo(veiculo);

        // Assert
        verify(restTemplate, times(1)).postForEntity(eq(url), any(SincronizarVeiculoRequestDto.class), eq(Void.class));
        verify(veiculoRepository, times(1)).marcarComoSincronizado(id);
    }

    @Test
    void deveExecutarFallbackSincronizarEmCasoDeErro() {
        // Arrange
        UUID id = UUID.randomUUID();
        Veiculo veiculo = new Veiculo(id, "Honda", "Civic", 2021, "Cinza", new BigDecimal("120000.00"), "ABC1D23");
        RuntimeException exception = new RuntimeException("connection refused");

        // Act
        vendasVeiculoAdapterHttp.fallbackSincronizar(veiculo, exception);

        // Assert
        verify(veiculoRepository, times(1)).marcarComoFalhaSincronizacao(id);
    }

    @Test
    void deveRetornarTrueQuandoVeiculoForVendido() {
        // Arrange
        UUID id = UUID.randomUUID();
        String url = VENDAS_SERVICE_URL + "/veiculos/" + id + "/vendido";

        when(restTemplate.getForObject(eq(url), eq(Boolean.class))).thenReturn(true);

        // Act
        boolean resultado = vendasVeiculoAdapterHttp.verificarVeiculoVendido(id);

        // Assert
        assertTrue(resultado);
        verify(restTemplate, times(1)).getForObject(eq(url), eq(Boolean.class));
    }

    @Test
    void deveRetornarFalseQuandoVeiculoNaoForVendido() {
        // Arrange
        UUID id = UUID.randomUUID();
        String url = VENDAS_SERVICE_URL + "/veiculos/" + id + "/vendido";

        when(restTemplate.getForObject(eq(url), eq(Boolean.class))).thenReturn(false);

        // Act
        boolean resultado = vendasVeiculoAdapterHttp.verificarVeiculoVendido(id);

        // Assert
        assertFalse(resultado);
        verify(restTemplate, times(1)).getForObject(eq(url), eq(Boolean.class));
    }

    @Test
    void deveRetornarFalseQuandoResponseForNull() {
        // Arrange
        UUID id = UUID.randomUUID();
        String url = VENDAS_SERVICE_URL + "/veiculos/" + id + "/vendido";

        when(restTemplate.getForObject(eq(url), eq(Boolean.class))).thenReturn(null);

        // Act
        boolean resultado = vendasVeiculoAdapterHttp.verificarVeiculoVendido(id);

        // Assert
        assertFalse(resultado);
        verify(restTemplate, times(1)).getForObject(eq(url), eq(Boolean.class));
    }

    @Test
    void deveLancarServicoVendasIndisponivelExceptionQuandoOcorrerRestClientException() {
        // Arrange
        UUID id = UUID.randomUUID();
        String url = VENDAS_SERVICE_URL + "/veiculos/" + id + "/vendido";

        when(restTemplate.getForObject(eq(url), eq(Boolean.class))).thenThrow(new RestClientException("connection refused"));

        // Act & Assert
        assertThrows(ServicoVendasIndisponivelException.class, () -> {
            vendasVeiculoAdapterHttp.verificarVeiculoVendido(id);
        });
        verify(restTemplate, times(1)).getForObject(eq(url), eq(Boolean.class));
    }
}
