package br.com.cdb.bancoDigitalCdb.controller;


import br.com.cdb.bancoDigitalCdb.dto.ContratarSeguroRequestDTO;
import br.com.cdb.bancoDigitalCdb.dto.SeguroDetalhesDTO;
import br.com.cdb.bancoDigitalCdb.dto.SeguroDisponivelDTO;
import br.com.cdb.bancoDigitalCdb.dto.SeguroResponseDTO;
import br.com.cdb.bancoDigitalCdb.entity.Seguro;
import br.com.cdb.bancoDigitalCdb.handler.SeguroNaoEncontradoException;
import br.com.cdb.bancoDigitalCdb.repository.SeguroRepository;
import br.com.cdb.bancoDigitalCdb.service.SeguroService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/seguros")
@RequiredArgsConstructor
public class SeguroController {

    private final SeguroService seguroService;
    private final SeguroRepository seguroRepository;

    @PostMapping
    public ResponseEntity<SeguroResponseDTO>contratarSeguro(@RequestBody ContratarSeguroRequestDTO request){
        SeguroResponseDTO response = seguroService.contratarSeguro(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping
    public ResponseEntity<List<SeguroDisponivelDTO>> listarSegurosDisponiveis() {
        return ResponseEntity.ok(seguroService.listarSegurosDisponiveis());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SeguroDetalhesDTO>detalharSeguro(@PathVariable String id){
        return ResponseEntity.ok(seguroService.detalharSeguro(id));
    }

    @GetMapping("/cartao/{cartaoId}")
    public ResponseEntity<List<SeguroResponseDTO>> listarSegurosPorCartao(
            @PathVariable String cartaoId) {
        return ResponseEntity.ok(seguroService.listarSegurosPorCartao(cartaoId));
    }
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelarSeguro(@PathVariable String id) {
        seguroService.cancelarSeguro(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/apolice/{numeroApolice}")
    public ResponseEntity<SeguroDetalhesDTO> obterSeguroPorApolice(
            @PathVariable String numeroApolice) {
        Seguro seguro = seguroRepository.findByNumeroApolice(numeroApolice)
                .orElseThrow(() -> new SeguroNaoEncontradoException("Seguro não encontrado"));

        return ResponseEntity.ok(seguroService.detalharSeguro(seguro.getId()));
    }
}
