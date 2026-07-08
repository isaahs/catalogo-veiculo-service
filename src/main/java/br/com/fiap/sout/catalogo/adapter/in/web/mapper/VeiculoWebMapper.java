package br.com.fiap.sout.catalogo.adapter.in.web.mapper;

import br.com.fiap.sout.catalogo.adapter.in.web.dto.request.VeiculoRequestDto;
import br.com.fiap.sout.catalogo.adapter.in.web.dto.response.VeiculoResponseDto;
import br.com.fiap.sout.catalogo.domain.model.Veiculo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface VeiculoWebMapper {

    @Mapping(target = "id", ignore = true)
    Veiculo toDomain(VeiculoRequestDto veiculoRequest);

    @Mapping(target = "id", source = "id")
    Veiculo toDomain(UUID id, VeiculoRequestDto dto);

    VeiculoResponseDto toResponse (Veiculo veiculo);

    List<VeiculoResponseDto> toResponseList(List<Veiculo> veiculos);
}
