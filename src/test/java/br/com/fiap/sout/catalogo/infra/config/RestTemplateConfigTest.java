package br.com.fiap.sout.catalogo.infra.config;

import br.com.fiap.sout.catalogo.infra.security.JwtClientHttpRequestInterceptor;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RestTemplateConfigTest {

    @Test
    void deveCriarRestTemplateComInterceptor() {
        JwtClientHttpRequestInterceptor interceptor = mock(JwtClientHttpRequestInterceptor.class);
        RestTemplateConfig config = new RestTemplateConfig();
        RestTemplate restTemplate = config.restTemplate(interceptor);
        assertNotNull(restTemplate);
        assertEquals(1, restTemplate.getInterceptors().size());
        assertEquals(interceptor, restTemplate.getInterceptors().get(0));
    }
}
