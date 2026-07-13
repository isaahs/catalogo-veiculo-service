package br.com.fiap.sout.catalogo.adapter.out.persistence.mapper;

import br.com.fiap.sout.catalogo.adapter.out.persistence.entity.VeiculoEntity;
import br.com.fiap.sout.catalogo.adapter.out.persistence.enums.StatusSincronizacao;
import br.com.fiap.sout.catalogo.domain.model.Veiculo;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class VeiculoEntityMapperTest {

    private final VeiculoEntityMapper mapper = Mappers.getMapper(VeiculoEntityMapper.class);

    @Test
    void deveMapearParaDomain() {
        UUID id = UUID.randomUUID();
        VeiculoEntity entity = new VeiculoEntity(id, "Honda", "Civic", 2021, "Cinza", new BigDecimal("120000.00"), StatusSincronizacao.PENDENTE, "ABC1D23");
        Veiculo domain = mapper.toDomain(entity);

        assertNotNull(domain);
        assertEquals(id, domain.id());
        assertEquals("Honda", domain.marca());
        assertEquals("Civic", domain.modelo());
        assertEquals(2021, domain.ano());
        assertEquals("Cinza", domain.cor());
        assertEquals(new BigDecimal("120000.00"), domain.preco());
        assertEquals("ABC1D23", domain.placa());
    }

    @Test
    void deveMapearParaVeiculoEntity() {
        UUID id = UUID.randomUUID();
        Veiculo domain = new Veiculo(id, "Honda", "Civic", 2021, "Cinza", new BigDecimal("120000.00"), "ABC1D23");
        VeiculoEntity entity = mapper.toVeiculoEntity(domain);

        assertNotNull(entity);
        assertEquals(id, entity.getId());
        assertEquals("Honda", entity.getMarca());
        assertEquals("Civic", entity.getModelo());
        assertEquals(2021, entity.getAno());
        assertEquals("Cinza", entity.getCor());
        assertEquals(new BigDecimal("120000.00"), entity.getPreco());
        assertEquals("ABC1D23", entity.getPlaca());
        assertEquals(StatusSincronizacao.PENDENTE, entity.getStatusSincronizacao());
    }

    @Test
    void deveRetornarNullQuandoMapearNull() {
        assertNull(mapper.toDomain(null));
        assertNull(mapper.toVeiculoEntity(null));
    }
}
