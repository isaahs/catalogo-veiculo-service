package br.com.fiap.sout.catalogo.adapter.out.persistence;

import br.com.fiap.sout.catalogo.adapter.out.persistence.entity.VeiculoEntity;
import br.com.fiap.sout.catalogo.adapter.out.persistence.enums.StatusSincronizacao;
import br.com.fiap.sout.catalogo.adapter.out.persistence.mapper.VeiculoEntityMapper;
import br.com.fiap.sout.catalogo.adapter.out.persistence.repository.VeiculoJpaRepository;
import br.com.fiap.sout.catalogo.domain.model.Veiculo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VeiculoRepositoryAdapterTest {

    @Mock
    private VeiculoJpaRepository jpaRepository;

    @Mock
    private VeiculoEntityMapper mapper;

    @InjectMocks
    private VeiculoRepositoryAdapter repositoryAdapter;

    @Test
    void deveSalvarVeiculoEInicializarComoPendente() {
        // Arrange
        UUID id = UUID.randomUUID();
        Veiculo veiculo = new Veiculo(id, "Honda", "Civic", 2021, "Cinza", new BigDecimal("120000.00"), "ABC1D23");
        VeiculoEntity entityInput = new VeiculoEntity(id, "Honda", "Civic", 2021, "Cinza", new BigDecimal("120000.00"), StatusSincronizacao.PENDENTE, "ABC1D23");
        VeiculoEntity entitySalva = new VeiculoEntity(id, "Honda", "Civic", 2021, "Cinza", new BigDecimal("120000.00"), StatusSincronizacao.PENDENTE, "ABC1D23");

        when(mapper.toVeiculoEntity(veiculo)).thenReturn(entityInput);
        when(jpaRepository.save(entityInput)).thenReturn(entitySalva);
        when(mapper.toDomain(entitySalva)).thenReturn(veiculo);

        // Act
        Veiculo salvo = repositoryAdapter.salvar(veiculo);

        // Assert
        assertNotNull(salvo);
        assertEquals(id, salvo.id());
        verify(mapper, times(1)).toVeiculoEntity(veiculo);
        verify(jpaRepository, times(1)).save(entityInput);
        verify(mapper, times(1)).toDomain(entitySalva);
    }

    @Test
    void deveBuscarNaoSincronizados() {
        // Arrange
        VeiculoEntity entity = new VeiculoEntity(UUID.randomUUID(), "Honda", "Civic", 2021, "Cinza", new BigDecimal("120000.00"), StatusSincronizacao.PENDENTE, "ABC1D23");
        Veiculo domain = new Veiculo(entity.getId(), "Honda", "Civic", 2021, "Cinza", new BigDecimal("120000.00"), "ABC1D23");

        when(jpaRepository.findByStatusSincronizacaoIn(List.of(StatusSincronizacao.PENDENTE, StatusSincronizacao.ERRO_REDE)))
                .thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        // Act
        List<Veiculo> resultado = repositoryAdapter.buscarNaoSincronizados();

        // Assert
        assertEquals(1, resultado.size());
        assertEquals(domain, resultado.get(0));
        verify(jpaRepository, times(1)).findByStatusSincronizacaoIn(any());
        verify(mapper, times(1)).toDomain(entity);
    }

    @Test
    void deveMarcarComoSincronizado() {
        // Arrange
        UUID id = UUID.randomUUID();
        VeiculoEntity entity = new VeiculoEntity(id, "Honda", "Civic", 2021, "Cinza", new BigDecimal("120000.00"), StatusSincronizacao.PENDENTE, "ABC1D23");

        when(jpaRepository.findById(id)).thenReturn(Optional.of(entity));

        // Act
        repositoryAdapter.marcarComoSincronizado(id);

        // Assert
        assertEquals(StatusSincronizacao.SINCRONIZADO, entity.getStatusSincronizacao());
        verify(jpaRepository, times(1)).findById(id);
        verify(jpaRepository, times(1)).save(entity);
    }

    @Test
    void deveMarcarComoFalhaSincronizacao() {
        // Arrange
        UUID id = UUID.randomUUID();
        VeiculoEntity entity = new VeiculoEntity(id, "Honda", "Civic", 2021, "Cinza", new BigDecimal("120000.00"), StatusSincronizacao.PENDENTE, "ABC1D23");

        when(jpaRepository.findById(id)).thenReturn(Optional.of(entity));

        // Act
        repositoryAdapter.marcarComoFalhaSincronizacao(id);

        // Assert
        assertEquals(StatusSincronizacao.ERRO_REDE, entity.getStatusSincronizacao());
        verify(jpaRepository, times(1)).findById(id);
        verify(jpaRepository, times(1)).save(entity);
    }

    @Test
    void deveBuscarVeiculoPorIdQuandoExiste() {
        // Arrange
        UUID id = UUID.randomUUID();
        VeiculoEntity entity = new VeiculoEntity(id, "Honda", "Civic", 2021, "Cinza", new BigDecimal("120000.00"), StatusSincronizacao.PENDENTE, "ABC1D23");
        Veiculo domain = new Veiculo(id, "Honda", "Civic", 2021, "Cinza", new BigDecimal("120000.00"), "ABC1D23");

        when(jpaRepository.findById(id)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        // Act
        Optional<Veiculo> resultado = repositoryAdapter.buscarPorId(id);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(domain, resultado.get());
        verify(jpaRepository, times(1)).findById(id);
        verify(mapper, times(1)).toDomain(entity);
    }

    @Test
    void deveRetornarOptionalEmptyQuandoBuscarPorIdInexistente() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(jpaRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        Optional<Veiculo> resultado = repositoryAdapter.buscarPorId(id);

        // Assert
        assertTrue(resultado.isEmpty());
        verify(jpaRepository, times(1)).findById(id);
        verifyNoInteractions(mapper);
    }

    @Test
    void deveListarTodosOsVeiculosComSucesso() {
        // Arrange
        VeiculoEntity entity1 = new VeiculoEntity(UUID.randomUUID(), "Honda", "Civic", 2021, "Cinza", new BigDecimal("120000.00"), StatusSincronizacao.PENDENTE, "ABC1D23");
        VeiculoEntity entity2 = new VeiculoEntity(UUID.randomUUID(), "Toyota", "Corolla", 2022, "Branco", new BigDecimal("130000.00"), StatusSincronizacao.PENDENTE, "XYZ9E87");
        Veiculo domain1 = new Veiculo(entity1.getId(), "Honda", "Civic", 2021, "Cinza", new BigDecimal("120000.00"), "ABC1D23");
        Veiculo domain2 = new Veiculo(entity2.getId(), "Toyota", "Corolla", 2022, "Branco", new BigDecimal("130000.00"), "XYZ9E87");

        when(jpaRepository.findAll()).thenReturn(List.of(entity1, entity2));
        when(mapper.toDomain(entity1)).thenReturn(domain1);
        when(mapper.toDomain(entity2)).thenReturn(domain2);

        // Act
        List<Veiculo> resultado = repositoryAdapter.listarTodos();

        // Assert
        assertEquals(2, resultado.size());
        assertEquals(domain1, resultado.get(0));
        assertEquals(domain2, resultado.get(1));
        verify(jpaRepository, times(1)).findAll();
        verify(mapper, times(1)).toDomain(entity1);
        verify(mapper, times(1)).toDomain(entity2);
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHouverVeiculosNaListagem() {
        // Arrange
        when(jpaRepository.findAll()).thenReturn(List.of());

        // Act
        List<Veiculo> resultado = repositoryAdapter.listarTodos();

        // Assert
        assertTrue(resultado.isEmpty());
        verify(jpaRepository, times(1)).findAll();
        verifyNoInteractions(mapper);
    }
}
