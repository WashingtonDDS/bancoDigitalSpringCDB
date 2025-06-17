package br.com.cdb.bancoDigitalCdb.controller;


import br.com.cdb.bancoDigitalCdb.dto.ClienteResponseDTO;
import br.com.cdb.bancoDigitalCdb.entity.Cliente;
import br.com.cdb.bancoDigitalCdb.service.ClienteService;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cliente")
public class ClienteController {
    ClienteService clienteService;

    @Autowired
    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping("/clientes")
    public ResponseEntity<List<Cliente>> getAllClientes() {
        return ResponseEntity.ok(clienteService.listarTodosClientes());
    }

    @GetMapping("/clientes/{cpf}")
    public ResponseEntity<ClienteResponseDTO>detalharClientePorCpf(
            @RequestParam @Pattern
                    (regexp =  "\\d{11}", message = "CPF deve conter 11 dígitos")String cpf){
        Cliente cliente = clienteService.detalharCliente(cpf);
        return ResponseEntity.ok(new ClienteResponseDTO(cliente));

    }
}
