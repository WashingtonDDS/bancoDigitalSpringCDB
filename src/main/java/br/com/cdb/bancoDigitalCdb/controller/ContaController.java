package br.com.cdb.bancoDigitalCdb.controller;

import br.com.cdb.bancoDigitalCdb.dto.*;
import br.com.cdb.bancoDigitalCdb.entity.*;


import br.com.cdb.bancoDigitalCdb.repository.ClienteRepository;
import br.com.cdb.bancoDigitalCdb.repository.ContaRepository;
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
    private final ClienteRepository clienteRepository;
    private final ContaRepository contaRepository;



    @PostMapping("/register")
    public ResponseEntity register (@RequestBody RegisterRequestDTO body ){
        try {
            Cliente newCliente = clienteService.criarClienteComEndereco(body);

            boolean temCorrente = Boolean.TRUE.equals(body.criarContaCorrente());
            boolean temPoupanca = Boolean.TRUE.equals(body.criarContaPoupanca());

            if (temCorrente) {
                contaService.criarContaCorrente(newCliente);
            }
            if (temPoupanca) {
                contaService.criarContaPoupanca(newCliente);
            }

            if (temCorrente && temPoupanca) {
                newCliente.setTipoDeConta(TipoDeConta.CORRENTE_POUPANCA);
            } else if (temCorrente) {
                newCliente.setTipoDeConta(TipoDeConta.CORRENTE);
            } else if (temPoupanca) {
                newCliente.setTipoDeConta(TipoDeConta.POUPANCA);
            }
            clienteRepository.save(newCliente);
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
    @PostMapping("/clientes/{clienteId}/contas")
    public ResponseEntity<String> adicionarConta(
            @PathVariable String clienteId,
            @RequestBody CriarContaRequest request
    ) {
        try {
            Cliente cliente = clienteService.buscarClientePorId(clienteId);
            boolean temContaCorrenteAntes = contaRepository.existsByClienteAndTipo(cliente, ContaCorrente.class);
            boolean temContaPoupancaAntes = contaRepository.existsByClienteAndTipo(cliente, ContaPoupanca.class);

            if (request.tipoDeConta() == TipoDeConta.CORRENTE) {
                contaService.criarContaCorrente(cliente);
            } else if (request.tipoDeConta() == TipoDeConta.POUPANCA) {
                contaService.criarContaPoupanca(cliente);
            }

            boolean temCorrente = contaRepository.existsByClienteAndTipo(cliente, ContaCorrente.class);
            boolean temPoupanca = contaRepository.existsByClienteAndTipo(cliente, ContaPoupanca.class);

            if (temCorrente && temPoupanca) {
                cliente.setTipoDeConta(TipoDeConta.CORRENTE_POUPANCA);
            } else if (temCorrente) {
                cliente.setTipoDeConta(TipoDeConta.CORRENTE);
            } else if (temPoupanca) {
                cliente.setTipoDeConta(TipoDeConta.POUPANCA);
            }

            clienteRepository.save(cliente);


            return ResponseEntity.ok("Conta adicionada com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao adicionar conta: " + e.getMessage());
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
            @RequestBody DepositoRequestDTO request) {
        contaService.realizarDeposito(id, request);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/{id}/saldo")
    public ResponseEntity<SaldoResponseDTO> consultarSaldo(@PathVariable String id) {
        return ResponseEntity.ok(contaService.consultarSaldo(id));
    }

    @PostMapping("/{id}/transferencia")
    public ResponseEntity<Void> transferir(@PathVariable String id, @RequestBody TransferenciaRequestDTO request) {
        contaService.transferir(id, request);
        return ResponseEntity.ok().build();

    }
    @PostMapping("/{id}/pix")
    public ResponseEntity<Void> realizarPix(@PathVariable String id, @RequestBody PixRequestDTO pixRequest) {
        contaService.fazerPix(id, pixRequest);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/{id}/saque")
    public ResponseEntity<Void> realizarSaque(
            @PathVariable String id,
            @RequestBody SaqueRequestDTO request) {
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
