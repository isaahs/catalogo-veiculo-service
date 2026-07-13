package br.com.fiap.sout.catalogo.adapter.out.persistence.enums;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StatusSincronizacaoTest {
    @Test
    void testEnumValues() {
        assertNotNull(StatusSincronizacao.valueOf("PENDENTE"));
        assertNotNull(StatusSincronizacao.valueOf("SINCRONIZADO"));
        assertNotNull(StatusSincronizacao.valueOf("ERRO_REDE"));
        assertEquals(3, StatusSincronizacao.values().length);
    }
}
