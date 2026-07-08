package br.com.fiap.sout.catalogo.infra.config;

import br.com.fiap.sout.catalogo.application.ports.in.AtualizarVeiculoPort;
import br.com.fiap.sout.catalogo.application.ports.in.BuscarVeiculoPorIdPort;
import br.com.fiap.sout.catalogo.application.ports.in.CadastrarVeiculoPort;
import br.com.fiap.sout.catalogo.application.ports.in.ListarVeiculoPort;
import br.com.fiap.sout.catalogo.application.ports.out.SincronizarCatalogoPort;
import br.com.fiap.sout.catalogo.application.ports.out.VeiculoRepositoryPort;
import br.com.fiap.sout.catalogo.application.ports.out.VerificarVeiculoVendidoPort;
import br.com.fiap.sout.catalogo.application.usecases.AtualizarVeiculoUseCase;
import br.com.fiap.sout.catalogo.application.usecases.BuscarVeiculoPorIdUseCase;
import br.com.fiap.sout.catalogo.application.usecases.CadastrarVeiculoUseCase;
import br.com.fiap.sout.catalogo.application.usecases.ListarVeiculoUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public BuscarVeiculoPorIdPort buscarVeiculoPorIdPort(VeiculoRepositoryPort veiculoRepositoryPort){
        return new BuscarVeiculoPorIdUseCase(veiculoRepositoryPort);
    }

    @Bean
    public CadastrarVeiculoPort cadastrarVeiculoPort(VeiculoRepositoryPort veiculoRepositoryPort, SincronizarCatalogoPort sincronizarCatalogoPort){
        return new CadastrarVeiculoUseCase(veiculoRepositoryPort, sincronizarCatalogoPort);
    }

    @Bean
    public ListarVeiculoPort listarVeiculoPort(VeiculoRepositoryPort veiculoRepositoryPort){
        return new ListarVeiculoUseCase(veiculoRepositoryPort);
    }

    @Bean
    public AtualizarVeiculoPort atualizarVeiculoPort(VeiculoRepositoryPort veiculoRepositoryPort, SincronizarCatalogoPort sincronizarCatalogoPort, VerificarVeiculoVendidoPort verificarVeiculoVendidoPort){
        return new AtualizarVeiculoUseCase(veiculoRepositoryPort, sincronizarCatalogoPort, verificarVeiculoVendidoPort);
    }

}
