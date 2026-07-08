package br.com.fiap.sout.catalogo.adapter.out.persistence.repository;

import br.com.fiap.sout.catalogo.adapter.out.persistence.entity.VeiculoEntity;
import br.com.fiap.sout.catalogo.adapter.out.persistence.enums.StatusSincronizacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface VeiculoJpaRepository extends JpaRepository<VeiculoEntity, UUID> {

    List<VeiculoEntity> findByStatusSincronizacaoIn(List<StatusSincronizacao> statusList);

}
