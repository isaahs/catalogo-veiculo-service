package br.com.fiap.sout.catalogo.adapter.in.web.mapper;

import br.com.fiap.sout.catalogo.adapter.in.web.dto.request.VeiculoRequestDto;
import br.com.fiap.sout.catalogo.adapter.in.web.dto.response.VeiculoResponseDto;
import br.com.fiap.sout.catalogo.domain.model.Veiculo;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class VeiculoWebMapperTest {

    private final VeiculoWebMapper mapper = Mappers.getMapper(VeiculoWebMapper.class);

    @Test
    void deveMapearParaDomainApenasComRequest() {
        VeiculoRequestDto request = new VeiculoRequestDto("Ford", "Ka", 2019, "Prata", new BigDecimal("40000.00"), "ABC1234");
        Veiculo domain = mapper.toDomain(request);

        assertNotNull(domain);
        assertNull(domain.id());
        assertEquals("Ford", domain.marca());
        assertEquals("Ka", domain.modelo());
        assertEquals(2019, domain.ano());
        assertEquals("Prata", domain.cor());
        assertEquals(new BigDecimal("40000.00"), domain.preco());
        assertEquals("ABC1234", domain.placa());
    }

    @Test
    void deveMapearParaDomainComIdERequest() {
        UUID id = UUID.randomUUID();
        VeiculoRequestDto request = new VeiculoRequestDto("Ford", "Ka", 2019, "Prata", new BigDecimal("40000.00"), "ABC1234");
        Veiculo domain = mapper.toDomain(id, request);

        assertNotNull(domain);
        assertEquals(id, domain.id());
        assertEquals("Ford", domain.marca());
        assertEquals("Ka", domain.modelo());
        assertEquals(2019, domain.ano());
        assertEquals("Prata", domain.cor());
        assertEquals(new BigDecimal("40000.00"), domain.preco());
        assertEquals("ABC1234", domain.placa());
    }

    @Test
    void deveMapearParaResponse() {
        UUID id = UUID.randomUUID();
        Veiculo domain = new Veiculo(id, "Ford", "Ka", 2019, "Prata", new BigDecimal("40000.00"), "ABC1234");
        VeiculoResponseDto response = mapper.toResponse(domain);

        assertNotNull(response);
        assertEquals(id, response.id());
        assertEquals("Ford", response.marca());
        assertEquals("Ka", response.modelo());
        assertEquals(2019, response.ano());
        assertEquals("Prata", response.cor());
        assertEquals(new BigDecimal("40000.00"), response.preco());
        assertEquals("ABC1234", response.placa());
    }

    @Test
    void deveMapearListaParaResponseList() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        Veiculo domain1 = new Veiculo(id1, "Ford", "Ka", 2019, "Prata", new BigDecimal("40000.00"), "ABC1234");
        Veiculo domain2 = new Veiculo(id2, "Chevrolet", "Onix", 2020, "Preto", new BigDecimal("50000.00"), "XYZ9876");

        List<VeiculoResponseDto> responses = mapper.toResponseList(List.of(domain1, domain2));

        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals(id1, responses.get(0).id());
        assertEquals(id2, responses.get(1).id());
    }

    @Test
    void deveRetornarNullQuandoMapearNull() {
        assertNull(mapper.toDomain(null));
        assertNull(mapper.toDomain(null, null));
        assertNull(mapper.toResponse(null));
        assertNull(mapper.toResponseList(null));

        UUID id = UUID.randomUUID();
        Veiculo domain = mapper.toDomain(id, null);
        assertNotNull(domain);
        assertEquals(id, domain.id());
        assertNull(domain.marca());
    }
}
