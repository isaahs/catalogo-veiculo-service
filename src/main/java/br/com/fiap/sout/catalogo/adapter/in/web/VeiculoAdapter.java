package br.com.fiap.sout.catalogo.adapter.in.web;

import br.com.fiap.sout.catalogo.adapter.in.web.dto.request.VeiculoRequestDto;
import br.com.fiap.sout.catalogo.adapter.in.web.dto.response.VeiculoResponseDto;
import br.com.fiap.sout.catalogo.adapter.in.web.mapper.VeiculoWebMapper;
import br.com.fiap.sout.catalogo.application.ports.in.AtualizarVeiculoPort;
import br.com.fiap.sout.catalogo.application.ports.in.BuscarVeiculoPorIdPort;
import br.com.fiap.sout.catalogo.application.ports.in.CadastrarVeiculoPort;
import br.com.fiap.sout.catalogo.application.ports.in.ListarVeiculoPort;
import br.com.fiap.sout.catalogo.domain.model.Veiculo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/veiculos")
@RequiredArgsConstructor
public class VeiculoAdapter {

    private final VeiculoWebMapper mapper;
    private final CadastrarVeiculoPort cadastrarPort;
    private final BuscarVeiculoPorIdPort buscarPort;
    private final ListarVeiculoPort listarPort;
    private final AtualizarVeiculoPort atualizarPort;


    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public VeiculoResponseDto cadastrarVeiculo(@Valid @RequestBody VeiculoRequestDto veiculoRequest) {
        Veiculo veiculoCadastrar = mapper.toDomain(veiculoRequest);
        Veiculo veiculoSalvo = cadastrarPort.cadastrar(veiculoCadastrar);
        return mapper.toResponse(veiculoSalvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VeiculoResponseDto> atualizarVeiculo(@PathVariable UUID id, @Valid @RequestBody VeiculoRequestDto veiculoRequest) {
        Veiculo veiculoAtualizar = mapper.toDomain(id, veiculoRequest);

        Veiculo veiculoAtualizado = atualizarPort.atualizarVeiculo(veiculoAtualizar);
        return ResponseEntity.ok(mapper.toResponse(veiculoAtualizado));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VeiculoResponseDto> buscarVeiculoPorId(@PathVariable UUID id) {
        Veiculo veiculo = buscarPort.buscarVeiculoPorId(id);
        return ResponseEntity.ok(mapper.toResponse(veiculo));
    }

    @GetMapping()
    public ResponseEntity<List<VeiculoResponseDto>> listarVeiculos() {
        List<Veiculo> veiculos = listarPort.listarVeiculos();
        return ResponseEntity.ok(mapper.toResponseList(veiculos));
    }
}
