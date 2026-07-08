package br.com.fiap.sout.catalogo.infra.config;

import br.com.fiap.sout.catalogo.infra.security.JwtClientHttpRequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(JwtClientHttpRequestInterceptor jwtInterceptor) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(List.of(jwtInterceptor));
        return restTemplate;
    }
}
