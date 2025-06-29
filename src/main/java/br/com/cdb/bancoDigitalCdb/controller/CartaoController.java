package br.com.cdb.bancoDigitalCdb.controller;

import br.com.cdb.bancoDigitalCdb.dto.*;
import br.com.cdb.bancoDigitalCdb.service.CartaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<CartaoDetalhesDTO>detalharCartao(@PathVariable String cartaoId){
        return ResponseEntity.ok(cartaoService.detalhaCartao(cartaoId));

    }

    @PostMapping("/{id}/pagamento")
    public ResponseEntity<Void>realizarPagamentoComCartao(@PathVariable String cartaoId, @RequestBody PagamentoCartaoRequestDTO request){
        cartaoService.realizarPagamentoComCartao(cartaoId,request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/limite")
    public ResponseEntity<Void>alterarLimiteCredito(@PathVariable String cartaoId, @RequestBody AlterarLimiteRequestDTO request){
        cartaoService.alterarLimiteCredito(cartaoId,request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Void> alterarStatus(
            @PathVariable String cartaoId,
            @RequestBody AlterarStatusRequestDTO request) {
        cartaoService.alterarStatus(cartaoId, request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/senha")
    public ResponseEntity<Void> alterarSenha(
            @PathVariable String cartaoId,
            @RequestBody AlterarSenhaRequestDTO request) {
        cartaoService.alterarSenha(cartaoId, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/fatura")
    public ResponseEntity<FaturaResponseDTO> consultarFatura(@PathVariable String cartaoId) {
        return ResponseEntity.ok(cartaoService.consultarFatura(cartaoId));
    }

    @PostMapping("/{id}/fatura/pagamento")
    public ResponseEntity<Void> pagarFatura(
            @PathVariable String cartaoId,
            @RequestBody PagamentoFaturaRequestDTO request) {
        cartaoService.pagarFatura(cartaoId, request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/limite-diario")
    public ResponseEntity<Void> alterarLimiteDiario(
            @PathVariable String cartaoId,
            @RequestBody AlterarLimiteRequestDTO request) {
        cartaoService.alterarLimiteDiario(cartaoId, request);
        return ResponseEntity.ok().build();
    }

}
