package br.com.fiap.sout.catalogo.infra.security;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtClientHttpRequestInterceptorTest {

    @Test
    void deveAdicionarTokenDeAutorizacao() throws IOException {
        JwtTokenProvider tokenProvider = mock(JwtTokenProvider.class);
        when(tokenProvider.gerarToken()).thenReturn("mocked-jwt-token");

        HttpRequest request = mock(HttpRequest.class);
        HttpHeaders headers = new HttpHeaders();
        when(request.getHeaders()).thenReturn(headers);

        byte[] body = new byte[0];
        ClientHttpRequestExecution execution = mock(ClientHttpRequestExecution.class);
        ClientHttpResponse response = mock(ClientHttpResponse.class);
        when(execution.execute(request, body)).thenReturn(response);

        JwtClientHttpRequestInterceptor interceptor = new JwtClientHttpRequestInterceptor(tokenProvider);
        ClientHttpResponse result = interceptor.intercept(request, body, execution);

        assertEquals(response, result);
        assertEquals("Bearer mocked-jwt-token", headers.getFirst(HttpHeaders.AUTHORIZATION));
        verify(tokenProvider, times(1)).gerarToken();
        verify(execution, times(1)).execute(request, body);
    }
}
