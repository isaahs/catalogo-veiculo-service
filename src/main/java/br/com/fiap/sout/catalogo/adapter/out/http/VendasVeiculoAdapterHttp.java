package br.com.fiap.sout.catalogo.adapter.out.http;

import br.com.fiap.sout.catalogo.application.ports.out.SincronizarCatalogoPort;
import br.com.fiap.sout.catalogo.application.ports.out.VeiculoRepositoryPort;
import br.com.fiap.sout.catalogo.application.ports.out.VerificarVeiculoVendidoPort;
import br.com.fiap.sout.catalogo.domain.model.Veiculo;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import br.com.fiap.sout.catalogo.adapter.out.http.dto.SincronizarVeiculoRequestDto;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class VendasVeiculoAdapterHttp implements SincronizarCatalogoPort, VerificarVeiculoVendidoPort {

    private final RestTemplate restTemplate;
    private final VeiculoRepositoryPort veiculoRepository;

    @Value("${vendas.service.url}")
    private String vendasServiceUrl;

    @Override
    @Retry(name = "sincronizarVeiculo", fallbackMethod = "fallbackSincronizar")
    public void sincronizarCatalogo(Veiculo veiculo) {

        String url = vendasServiceUrl + "/veiculos";
        SincronizarVeiculoRequestDto requestDto = SincronizarVeiculoRequestDto.from(veiculo);
        restTemplate.postForEntity(url, requestDto, Void.class);

        veiculoRepository.marcarComoSincronizado(veiculo.id());
    }

    @Override
    public boolean verificarVeiculoVendido(UUID Id) {

        String url = vendasServiceUrl + "/veiculos/" + Id + "/vendido";
        Boolean isVendido = restTemplate.getForObject(url, Boolean.class);
        if (isVendido != null) {
            return isVendido;
        }
        return false;
    }

    public void fallbackSincronizar(Veiculo veiculo, Throwable t) {
        log.error("Falha ao sincronizar veiculo {} após retentativas.Motivo:{}"
                , veiculo.id(), t.getMessage());
        veiculoRepository.marcarComoFalhaSincronizacao(veiculo.id());
    }
}
