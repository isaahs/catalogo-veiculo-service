package br.com.fiap.sout.catalogo.adapter.out.http.dto;

import br.com.fiap.sout.catalogo.domain.model.Veiculo;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SincronizarVeiculoRequestDtoTest {

    @Test
    void deveCriarDtoApartirDeVeiculo() {
        UUID id = UUID.randomUUID();
        Veiculo veiculo = new Veiculo(id, "Honda", "Civic", 2021, "Cinza", new BigDecimal("120000.00"), "ABC1D23");

        SincronizarVeiculoRequestDto dto = SincronizarVeiculoRequestDto.from(veiculo);

        assertNotNull(dto);
        assertEquals(id, dto.veiculoId());
        assertEquals("Honda", dto.marca());
        assertEquals("Civic", dto.modelo());
        assertEquals(2021, dto.ano());
        assertEquals("Cinza", dto.cor());
        assertEquals(new BigDecimal("120000.00"), dto.preco());
        assertEquals("ABC1D23", dto.placa());
    }

    @Test
    void deveTestarEqualsHashCodeEToString() {
        UUID id = UUID.randomUUID();
        SincronizarVeiculoRequestDto dto1 = new SincronizarVeiculoRequestDto(id, "Honda", "Civic", 2021, "Cinza", new BigDecimal("120000.00"), "ABC1D23");
        SincronizarVeiculoRequestDto dto2 = new SincronizarVeiculoRequestDto(id, "Honda", "Civic", 2021, "Cinza", new BigDecimal("120000.00"), "ABC1D23");
        SincronizarVeiculoRequestDto dto3 = new SincronizarVeiculoRequestDto(UUID.randomUUID(), "Honda", "Civic", 2021, "Cinza", new BigDecimal("120000.00"), "ABC1D23");

        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotNull(dto1.toString());
    }
}
