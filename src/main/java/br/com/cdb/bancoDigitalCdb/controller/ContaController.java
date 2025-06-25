package br.com.cdb.bancoDigitalCdb.controller;

import br.com.cdb.bancoDigitalCdb.dto.*;
import br.com.cdb.bancoDigitalCdb.entity.*;


import br.com.cdb.bancoDigitalCdb.security.TokenService;

import br.com.cdb.bancoDigitalCdb.service.ClienteService;
import br.com.cdb.bancoDigitalCdb.service.ContaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;



@RestController
@RequestMapping("/conta")
@RequiredArgsConstructor
public class ContaController {


    private final TokenService tokenService;
    private final ContaService contaService;
    private final ClienteService clienteService;



    @PostMapping("/register")
    public ResponseEntity register (@RequestBody RegisterRequestDTO body ){
        try {
            Cliente newCliente = clienteService.criarClienteComEndereco(body);

            if (body.tipoDeConta() == TipoDeConta.CORRENTE){
                ContaCorrente contaCorrente = contaService.criarContaCorrente(newCliente);
            } else if (body.tipoDeConta() == TipoDeConta.POUPANCA) {
                ContaPoupanca contaPoupanca = contaService.criarContaPoupanca(newCliente);
            }

            String token = this.tokenService.generateToken(newCliente);
            return ResponseEntity.ok(new ResponseDTO(newCliente.getNome(),token));
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }

    }
    @PostMapping("/login")
    public ResponseEntity login (@RequestBody LoginRequestDTO body ){
        try {
            Cliente cliente = clienteService.autenticar(body.email(), body.password());
            String token = this.tokenService.generateToken(cliente);
            return ResponseEntity.ok(new ResponseDTO(cliente.getNome(),token));
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/contas")
    public ResponseEntity<List<Conta>> listarTodasContas() {
        return ResponseEntity.ok(contaService.listarTodasContas());
    }
    @GetMapping("/contas/{id}")
    public ResponseEntity<Conta> detalharContaPorId(@PathVariable String id) {
        Conta conta = contaService.detalharConta(id);
        return ResponseEntity.ok(conta);
    }

    @PostMapping("/{id}/deposito")
    public ResponseEntity<Void> realizarDeposito(
            @PathVariable String id,
            @RequestBody DepositoRequest request) {
        contaService.realizarDeposito(id, request);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/{id}/saldo")
    public ResponseEntity<SaldoResponse> consultarSaldo(@PathVariable String id) {
        return ResponseEntity.ok(contaService.consultarSaldo(id));
    }

    @PostMapping("/{id}/transferencia")
    public ResponseEntity<Void> transferir(@PathVariable String id, @RequestBody TransferenciaRequest request) {
        contaService.transferir(id, request);
        return ResponseEntity.ok().build();

    }
    @PostMapping("/{id}/pix")
    public ResponseEntity<Void> realizarPix(@PathVariable String id, @RequestBody PixRequest pixRequest) {
        contaService.fazerPix(id, pixRequest);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/{id}/saque")
    public ResponseEntity<Void> realizarSaque(
            @PathVariable String id,
            @RequestBody SaqueRequest request) {
        contaService.realizarSaque(id, request);
        return ResponseEntity.ok().build();
    }
    @PutMapping("/{id}/manutencao")
    public ResponseEntity<Void> aplicarTaxaManutencao(@PathVariable String id) {
        contaService.aplicarTaxaManutencao(id);
        return ResponseEntity.ok().build();
    }
    @PutMapping("/{id}/rendimentos")
    public ResponseEntity<Void> aplicarRendimentos(@PathVariable String id) {
        contaService.aplicarRendimento(id);
        return ResponseEntity.ok().build();
    }


}
