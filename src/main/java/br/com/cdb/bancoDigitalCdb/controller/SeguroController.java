package br.com.cdb.bancoDigitalCdb.controller;


import br.com.cdb.bancoDigitalCdb.dto.*;
import br.com.cdb.bancoDigitalCdb.entity.Seguro;
import br.com.cdb.bancoDigitalCdb.handler.SeguroNaoEncontradoException;
import br.com.cdb.bancoDigitalCdb.repository.SeguroRepository;
import br.com.cdb.bancoDigitalCdb.security.CachedBodyHttpServletRequest;
import br.com.cdb.bancoDigitalCdb.service.SeguroService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(SeguroController.class);


    @PostMapping("/viagem")
    public ResponseEntity<?> contratarSeguroViagem(HttpServletRequest rawRequest) {

        try {

            CachedBodyHttpServletRequest requestWrapper = (CachedBodyHttpServletRequest) rawRequest;


            String requestBody = new String(requestWrapper.getCachedBody());
            logger.info("Corpo recebido: {}", requestBody);


            ObjectMapper mapper = new ObjectMapper();
            ContratarSeguroViagemDTO request = mapper.readValue(requestBody, ContratarSeguroViagemDTO.class);

            logger.info("Dados convertidos: cartaoId={}, adquirirSeguroViagem={}",
                    request.cartaoId(), request.adquirirSeguroViagem());


            SeguroResponseDTO response = seguroService.contratarSeguroViagem(request.cartaoId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            logger.error("Erro ao processar requisição", e);
            return ResponseEntity.badRequest().body("Erro no processamento da requisição");
        }
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
