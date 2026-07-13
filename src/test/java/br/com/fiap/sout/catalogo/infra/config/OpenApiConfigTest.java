package br.com.fiap.sout.catalogo.infra.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OpenApiConfigTest {

    @Test
    void deveCriarOpenAPI() {
        OpenApiConfig config = new OpenApiConfig();
        OpenAPI openAPI = config.customOpenAPI();
        assertNotNull(openAPI);
        assertEquals("Vehicle Catalog Service API", openAPI.getInfo().getTitle());
        assertTrue(openAPI.getComponents().getSecuritySchemes().containsKey("BearerAuth"));
    }
}
