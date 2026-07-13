package br.com.fiap.sout.catalogo.adapter.out.persistence.entity;

import br.com.fiap.sout.catalogo.adapter.out.persistence.enums.StatusSincronizacao;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class VeiculoEntityTest {

    @Test
    void deveTestarGettersESetters() {
        VeiculoEntity entity = new VeiculoEntity();
        UUID id = UUID.randomUUID();
        
        entity.setId(id);
        entity.setMarca("Ford");
        entity.setModelo("Ka");
        entity.setAno(2019);
        entity.setCor("Prata");
        entity.setPreco(new BigDecimal("40000.00"));
        entity.setStatusSincronizacao(StatusSincronizacao.SINCRONIZADO);
        entity.setPlaca("ABC1234");

        assertEquals(id, entity.getId());
        assertEquals("Ford", entity.getMarca());
        assertEquals("Ka", entity.getModelo());
        assertEquals(2019, entity.getAno());
        assertEquals("Prata", entity.getCor());
        assertEquals(new BigDecimal("40000.00"), entity.getPreco());
        assertEquals(StatusSincronizacao.SINCRONIZADO, entity.getStatusSincronizacao());
        assertEquals("ABC1234", entity.getPlaca());
    }

    @Test
    void deveTestarAllArgsConstructor() {
        UUID id = UUID.randomUUID();
        VeiculoEntity entity = new VeiculoEntity(id, "Ford", "Ka", 2019, "Prata", new BigDecimal("40000.00"), StatusSincronizacao.SINCRONIZADO, "ABC1234");

        assertEquals(id, entity.getId());
        assertEquals("Ford", entity.getMarca());
        assertEquals("Ka", entity.getModelo());
        assertEquals(2019, entity.getAno());
        assertEquals("Prata", entity.getCor());
        assertEquals(new BigDecimal("40000.00"), entity.getPreco());
        assertEquals(StatusSincronizacao.SINCRONIZADO, entity.getStatusSincronizacao());
        assertEquals("ABC1234", entity.getPlaca());
    }
}
