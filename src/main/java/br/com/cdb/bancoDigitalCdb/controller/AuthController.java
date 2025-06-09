package br.com.cdb.bancoDigitalCdb.controller;

import br.com.cdb.bancoDigitalCdb.dto.LoginRequestDTO;
import br.com.cdb.bancoDigitalCdb.dto.RegisterRequestDTO;
import br.com.cdb.bancoDigitalCdb.dto.ResponseDTO;
import br.com.cdb.bancoDigitalCdb.entity.Cliente;
import br.com.cdb.bancoDigitalCdb.repository.ClienteRepository;
import br.com.cdb.bancoDigitalCdb.service.security.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity login (@RequestBody LoginRequestDTO body ){
        Cliente cliente = this.clienteRepository.findByEmail(body.email()).orElseThrow(()-> new RuntimeException("User not found") );
        if (passwordEncoder.matches(body.password(), cliente.getPassword())){
            String token = this.tokenService.generateToken(cliente);
            return ResponseEntity.ok(new ResponseDTO(cliente.getName(),token));
        }
        return ResponseEntity.badRequest().build();
    }
    @PostMapping("/register")
    public ResponseEntity register (@RequestBody RegisterRequestDTO body ){
        Optional<Cliente> cliente = this.clienteRepository.findByEmail(body.email());
        if (cliente.isEmpty()){
            Cliente newCliente = new Cliente();
            newCliente.setPassword(passwordEncoder.encode(body.password()));
            newCliente.setEmail(body.email());
            newCliente.setName(body.name());
            this.clienteRepository.save(newCliente);

            String token = this.tokenService.generateToken(newCliente);
            return ResponseEntity.ok(new ResponseDTO(newCliente.getName(),token));

        }
        return ResponseEntity.badRequest().build();
    }
    @GetMapping("/clientess")
    public List<Cliente> getAllClientes() {
        return clienteRepository.findAll();
    }

}
