package br.com.fiap.sout.catalogo.infra.scheduler;

import br.com.fiap.sout.catalogo.application.ports.out.SincronizarCatalogoPort;
import br.com.fiap.sout.catalogo.application.ports.out.VeiculoRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VeiculoSyncScheduler {
    private final VeiculoRepositoryPort veiculoRepositoryPort;
    private final SincronizarCatalogoPort sincronizarCatalogoPort;

    @Scheduled(fixedDelay = 300000)
    private void sincronizarVeiculos() {
        veiculoRepositoryPort.buscarNaoSincronizados()
                .forEach(sincronizarCatalogoPort::sincronizarCatalogo);
    }
}
