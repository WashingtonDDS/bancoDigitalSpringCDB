package br.com.cdb.bancoDigitalCdb.controller;

import br.com.cdb.bancoDigitalCdb.dto.*;
import br.com.cdb.bancoDigitalCdb.service.CartaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/cartoes")
public class CartaoController {

    private final CartaoService cartaoService;

    @Autowired
    public CartaoController(CartaoService cartaoService) {
        this.cartaoService = cartaoService;
    }

    @PostMapping
    public ResponseEntity<CartaoResponseDTO>emitirCartao(@RequestBody CartaoRequestDTO request){
        return ResponseEntity.status(HttpStatus.CREATED).body(cartaoService.emitirCartao(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CartaoDetalhesDTO>detalharCartao(@PathVariable String id){
        return ResponseEntity.ok(cartaoService.detalhaCartao(id));

    }

    @PostMapping("/{id}/pagamento")
    public ResponseEntity<Void>realizarPagamentoComCartao(@PathVariable String id, @RequestBody PagamentoCartaoRequestDTO request){
        cartaoService.realizarPagamentoComCartao(id,request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/limite")
    public ResponseEntity<Void>alterarLimiteCredito(@PathVariable String id, @RequestBody AlterarLimiteRequestDTO request){
        cartaoService.alterarLimiteCredito(id,request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Void> alterarStatus(
            @PathVariable String id,
            @RequestBody AlterarStatusRequestDTO request) {
        cartaoService.alterarStatus(id, request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/senha")
    public ResponseEntity<Void> alterarSenha(
            @PathVariable String id,
            @RequestBody AlterarSenhaRequestDTO request) {
        cartaoService.alterarSenha(id, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/fatura")
    public ResponseEntity<FaturaResponseDTO> consultarFatura(@PathVariable String id) {
        return ResponseEntity.ok(cartaoService.consultarFatura(id));
    }

    @PostMapping("/{id}/fatura/pagamento")
    public ResponseEntity<Void> pagarFatura(
            @PathVariable String id,
            @RequestBody PagamentoFaturaRequestDTO request) {
        cartaoService.pagarFatura(id, request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/limite-diario")
    public ResponseEntity<Void> alterarLimiteDiario(
            @PathVariable String id,
            @RequestBody AlterarLimiteRequestDTO request) {
        cartaoService.alterarLimiteDiario(id, request);
        return ResponseEntity.ok().build();
    }

}
