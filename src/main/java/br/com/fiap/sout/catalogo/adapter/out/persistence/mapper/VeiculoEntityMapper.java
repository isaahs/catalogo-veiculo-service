package br.com.fiap.sout.catalogo.adapter.out.persistence.mapper;

import br.com.fiap.sout.catalogo.adapter.out.persistence.entity.VeiculoEntity;
import br.com.fiap.sout.catalogo.domain.model.Veiculo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VeiculoEntityMapper {

    Veiculo toDomain(VeiculoEntity veiculo);

    @Mapping(target = "statusSincronizacao", ignore = true)
    VeiculoEntity toVeiculoEntity(Veiculo veiculo);

}
