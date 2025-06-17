package br.com.cdb.bancoDigitalCdb.controller;

import br.com.cdb.bancoDigitalCdb.dto.LoginRequestDTO;
import br.com.cdb.bancoDigitalCdb.dto.RegisterRequestDTO;
import br.com.cdb.bancoDigitalCdb.dto.ResponseDTO;
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
            return ResponseEntity.ok(new ResponseDTO(newCliente.getName(),token));
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }

    }
    @PostMapping("/login")
    public ResponseEntity login (@RequestBody LoginRequestDTO body ){
        try {
            Cliente cliente = clienteService.autenticar(body.email(), body.password());
            String token = this.tokenService.generateToken(cliente);
            return ResponseEntity.ok(new ResponseDTO(cliente.getName(),token));
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/contas")
    public ResponseEntity<List<Conta>> listarTodasContas() {
        return ResponseEntity.ok(contaService.listarTodasContas());
    }


}
