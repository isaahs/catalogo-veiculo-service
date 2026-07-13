package br.com.fiap.sout.catalogo.adapter.in.web;

import br.com.fiap.sout.catalogo.adapter.in.web.dto.request.VeiculoRequestDto;
import br.com.fiap.sout.catalogo.adapter.in.web.dto.response.VeiculoResponseDto;
import br.com.fiap.sout.catalogo.adapter.in.web.mapper.VeiculoWebMapper;
import br.com.fiap.sout.catalogo.application.ports.in.AtualizarVeiculoPort;
import br.com.fiap.sout.catalogo.application.ports.in.BuscarVeiculoPorIdPort;
import br.com.fiap.sout.catalogo.application.ports.in.CadastrarVeiculoPort;
import br.com.fiap.sout.catalogo.application.ports.in.ListarVeiculoPort;
import br.com.fiap.sout.catalogo.domain.exceptions.PlacaAlteradaException;
import br.com.fiap.sout.catalogo.domain.exceptions.VeiculoNaoEncontradoException;
import br.com.fiap.sout.catalogo.domain.exceptions.VeiculoVendidoException;
import br.com.fiap.sout.catalogo.domain.model.Veiculo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VeiculoAdapter.class)
class VeiculoAdapterTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private VeiculoWebMapper mapper;

    @MockitoBean
    private CadastrarVeiculoPort cadastrarPort;

    @MockitoBean
    private BuscarVeiculoPorIdPort buscarPort;

    @MockitoBean
    private ListarVeiculoPort listarPort;

    @MockitoBean
    private AtualizarVeiculoPort atualizarPort;

    @Test
    void deveCadastrarVeiculoComSucesso() throws Exception {
        // Arrange
        VeiculoRequestDto request = new VeiculoRequestDto("Ford", "Ka", 2019, "Prata", new BigDecimal("40000.00"), "ABC1234");
        Veiculo veiculo = new Veiculo(null, "Ford", "Ka", 2019, "Prata", new BigDecimal("40000.00"), "ABC1234");
        Veiculo veiculoSalvo = new Veiculo(UUID.randomUUID(), "Ford", "Ka", 2019, "Prata", new BigDecimal("40000.00"), "ABC1234");
        VeiculoResponseDto response = new VeiculoResponseDto(veiculoSalvo.id(), "Ford", "Ka", 2019, "Prata", new BigDecimal("40000.00"), "ABC1234");

        when(mapper.toDomain(any(VeiculoRequestDto.class))).thenReturn(veiculo);
        when(cadastrarPort.cadastrar(any(Veiculo.class))).thenReturn(veiculoSalvo);
        when(mapper.toResponse(any(Veiculo.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/veiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(veiculoSalvo.id().toString()))
                .andExpect(jsonPath("$.placa").value("ABC1234"))
                .andExpect(jsonPath("$.marca").value("Ford"));
    }

    @Test
    void deveRetornarBadRequestQuandoPlacaForInvalida() throws Exception {
        // Arrange
        VeiculoRequestDto request = new VeiculoRequestDto("Ford", "Ka", 2019, "Prata", new BigDecimal("40000.00"), "PLACA-INVALIDA");

        // Act & Assert
        mockMvc.perform(post("/veiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveAtualizarVeiculoComSucesso() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        VeiculoRequestDto request = new VeiculoRequestDto("Ford", "Ka", 2019, "Prata", new BigDecimal("42000.00"), "ABC1234");
        Veiculo veiculo = new Veiculo(id, "Ford", "Ka", 2019, "Prata", new BigDecimal("42000.00"), "ABC1234");
        VeiculoResponseDto response = new VeiculoResponseDto(id, "Ford", "Ka", 2019, "Prata", new BigDecimal("42000.00"), "ABC1234");

        when(mapper.toDomain(eq(id), any(VeiculoRequestDto.class))).thenReturn(veiculo);
        when(atualizarPort.atualizarVeiculo(any(Veiculo.class))).thenReturn(veiculo);
        when(mapper.toResponse(any(Veiculo.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(put("/veiculos/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.preco").value(42000.00));
    }

    @Test
    void deveRetornarNotFoundQuandoAtualizarVeiculoNaoExistente() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        VeiculoRequestDto request = new VeiculoRequestDto("Ford", "Ka", 2019, "Prata", new BigDecimal("42000.00"), "ABC1234");
        Veiculo veiculo = new Veiculo(id, "Ford", "Ka", 2019, "Prata", new BigDecimal("42000.00"), "ABC1234");

        when(mapper.toDomain(eq(id), any(VeiculoRequestDto.class))).thenReturn(veiculo);
        when(atualizarPort.atualizarVeiculo(any(Veiculo.class))).thenThrow(new VeiculoNaoEncontradoException("Veículo não encontrado."));

        // Act & Assert
        mockMvc.perform(put("/veiculos/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Veículo não encontrado."));
    }

    @Test
    void deveRetornarConflictQuandoPlacaForAlteradaNaAtualizacao() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        VeiculoRequestDto request = new VeiculoRequestDto("Ford", "Ka", 2019, "Prata", new BigDecimal("42000.00"), "ABC1234");
        Veiculo veiculo = new Veiculo(id, "Ford", "Ka", 2019, "Prata", new BigDecimal("42000.00"), "ABC1234");

        when(mapper.toDomain(eq(id), any(VeiculoRequestDto.class))).thenReturn(veiculo);
        when(atualizarPort.atualizarVeiculo(any(Veiculo.class))).thenThrow(new PlacaAlteradaException("A placa do veículo não pode ser alterada."));

        // Act & Assert
        mockMvc.perform(put("/veiculos/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("A placa do veículo não pode ser alterada."));
    }

    @Test
    void deveRetornarConflictQuandoAtualizarVeiculoJaVendido() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        VeiculoRequestDto request = new VeiculoRequestDto("Ford", "Ka", 2019, "Prata", new BigDecimal("42000.00"), "ABC1234");
        Veiculo veiculo = new Veiculo(id, "Ford", "Ka", 2019, "Prata", new BigDecimal("42000.00"), "ABC1234");

        when(mapper.toDomain(eq(id), any(VeiculoRequestDto.class))).thenReturn(veiculo);
        when(atualizarPort.atualizarVeiculo(any(Veiculo.class))).thenThrow(new VeiculoVendidoException("Veículo já vendido, não é possível atualizar."));

        // Act & Assert
        mockMvc.perform(put("/veiculos/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("Veículo já vendido, não é possível atualizar."));
    }

    @Test
    void deveRetornarBadGatewayQuandoServicoVendasIndisponivel() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        VeiculoRequestDto request = new VeiculoRequestDto("Ford", "Ka", 2019, "Prata", new BigDecimal("42000.00"), "ABC1234");
        Veiculo veiculo = new Veiculo(id, "Ford", "Ka", 2019, "Prata", new BigDecimal("42000.00"), "ABC1234");

        when(mapper.toDomain(eq(id), any(VeiculoRequestDto.class))).thenReturn(veiculo);
        when(atualizarPort.atualizarVeiculo(any(Veiculo.class))).thenThrow(new br.com.fiap.sout.catalogo.domain.exceptions.ServicoVendasIndisponivelException(
                "Não foi possível verificar se o veículo está vendido porque o serviço de vendas está indisponível.", new RuntimeException("connection refused")));

        // Act & Assert
        mockMvc.perform(put("/veiculos/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.detail").value("Não foi possível verificar se o veículo está vendido porque o serviço de vendas está indisponível."));
    }

    @Test
    void deveBuscarVeiculoPorIdComSucesso() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        Veiculo veiculo = new Veiculo(id, "Ford", "Ka", 2019, "Prata", new BigDecimal("40000.00"), "ABC1234");
        VeiculoResponseDto response = new VeiculoResponseDto(id, "Ford", "Ka", 2019, "Prata", new BigDecimal("40000.00"), "ABC1234");

        when(buscarPort.buscarVeiculoPorId(id)).thenReturn(veiculo);
        when(mapper.toResponse(veiculo)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/veiculos/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.placa").value("ABC1234"));
    }

    @Test
    void deveRetornarNotFoundQuandoBuscarVeiculoInexistente() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        when(buscarPort.buscarVeiculoPorId(id)).thenThrow(new VeiculoNaoEncontradoException("Veículo não encontrado."));

        // Act & Assert
        mockMvc.perform(get("/veiculos/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Veículo não encontrado."));
    }

    @Test
    void deveListarVeiculosComSucesso() throws Exception {
        // Arrange
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        Veiculo veiculo1 = new Veiculo(id1, "Ford", "Ka", 2019, "Prata", new BigDecimal("40000.00"), "ABC1234");
        Veiculo veiculo2 = new Veiculo(id2, "Chevrolet", "Onix", 2020, "Preto", new BigDecimal("50000.00"), "XYZ9876");
        VeiculoResponseDto response1 = new VeiculoResponseDto(id1, "Ford", "Ka", 2019, "Prata", new BigDecimal("40000.00"), "ABC1234");
        VeiculoResponseDto response2 = new VeiculoResponseDto(id2, "Chevrolet", "Onix", 2020, "Preto", new BigDecimal("50000.00"), "XYZ9876");

        when(listarPort.listarVeiculos()).thenReturn(java.util.List.of(veiculo1, veiculo2));
        when(mapper.toResponseList(any())).thenReturn(java.util.List.of(response1, response2));

        // Act & Assert
        mockMvc.perform(get("/veiculos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(id1.toString()))
                .andExpect(jsonPath("$[1].id").value(id2.toString()));
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHouverVeiculos() throws Exception {
        // Arrange
        when(listarPort.listarVeiculos()).thenReturn(java.util.Collections.emptyList());
        when(mapper.toResponseList(any())).thenReturn(java.util.Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/veiculos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void deveRetornarInternalServerErrorQuandoOcorrerErroInesperado() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        when(buscarPort.buscarVeiculoPorId(id)).thenThrow(new RuntimeException("Erro genérico"));

        // Act & Assert
        mockMvc.perform(get("/veiculos/{id}", id))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.detail").value("Ocorreu um erro inesperado."));
    }
}
