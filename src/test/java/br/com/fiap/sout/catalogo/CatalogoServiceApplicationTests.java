package br.com.fiap.sout.catalogo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class CatalogoServiceApplicationTests {

    @Test
    void applicationStarts() {
        assertDoesNotThrow(() -> new CatalogoServiceApplication());
    }

}
