package br.com.cdb.bancoDigitalCdb.controller;

import br.com.cdb.bancoDigitalCdb.dto.AlterarSenha;
import br.com.cdb.bancoDigitalCdb.dto.MailBody;
import br.com.cdb.bancoDigitalCdb.entity.Cliente;
import br.com.cdb.bancoDigitalCdb.entity.EsqueciMinhaSenha;
import br.com.cdb.bancoDigitalCdb.handler.CampoObrigatorioException;
import br.com.cdb.bancoDigitalCdb.repository.ClienteRepository;
import br.com.cdb.bancoDigitalCdb.repository.EsqueciMinhaSenhaRepository;
import br.com.cdb.bancoDigitalCdb.security.SecurityConfig;
import br.com.cdb.bancoDigitalCdb.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

@RestController
@RequestMapping("/esqueciMinhasenha")
public class EsqueciMinhaSenhaController {

    private final ClienteRepository clienteRepository;
    private final EmailService emailService;
    private final EsqueciMinhaSenhaRepository esqueciMinhaSenhaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public EsqueciMinhaSenhaController(ClienteRepository clienteRepository, EmailService emailService, EsqueciMinhaSenhaRepository esqueciMinhaSenhaRepository) {
        this.clienteRepository = clienteRepository;
        this.emailService = emailService;
        this.esqueciMinhaSenhaRepository = esqueciMinhaSenhaRepository;

    }

    @PostMapping("/verificaMail/{email}")
    public ResponseEntity<String>verificaMail(@PathVariable String email){
        Cliente cliente = clienteRepository.findByEmail(email).orElseThrow(()-> new CampoObrigatorioException(email));

        int otp = geradorOTP();
        MailBody mailBody = MailBody.builder()
                .to(email)
                .text("Este é o código OTP para a sua solicitação de recuperação de senha: " + otp)
                .subject("Codigo OTP para redefinir a senha")
                .build();

        EsqueciMinhaSenha esqueciMinhaSenha = EsqueciMinhaSenha.builder()
                .otp(otp)
                .expirationTime(new Date(System.currentTimeMillis()+ 70 * 1000))
                .cliente(cliente)
                .build();

        emailService.sendSimpleMessage(mailBody);
        esqueciMinhaSenhaRepository.save(esqueciMinhaSenha);

        return ResponseEntity.ok("Codigo de verificação enviado para o email");
    }

    @PostMapping("/verificaOtp/{otp}/{email}")
    public ResponseEntity<String>verificarOPT(@PathVariable Integer otp, @PathVariable String email){
        Cliente cliente = clienteRepository.findByEmail(email).orElseThrow(()-> new CampoObrigatorioException(email));
       EsqueciMinhaSenha esqueciMinhaSenha = esqueciMinhaSenhaRepository
               .findByOtpCliente(otp, cliente).orElseThrow(()->new  RuntimeException("OTP para email: "+ email));
       if (esqueciMinhaSenha.getExpirationTime().before(Date.from(Instant.now()))){
           esqueciMinhaSenhaRepository.deleteById(esqueciMinhaSenha.getId());
           return new ResponseEntity<>("OTP expirado!", HttpStatus.EXPECTATION_FAILED);
       }
       return ResponseEntity.ok("OTP Verificado");
    }

    @PostMapping("/alterarSenha/{email}")
    public  ResponseEntity<String> alterarSenha(@RequestBody AlterarSenha alterarSenha, @PathVariable String email){
        if (!Objects.equals(alterarSenha.password(),alterarSenha.repetePassword())) {
            return new ResponseEntity<>("Por favor insira a senha novamente!", HttpStatus.EXPECTATION_FAILED);
        }
        String encodedSenha = passwordEncoder.encode(alterarSenha.password());
        clienteRepository.atualizaSenha(email,encodedSenha);

        return ResponseEntity.ok("A senha foi alterada!");
    }


    private Integer geradorOTP(){
        Random random = new Random();
        return random.nextInt(100_000, 999_999);
    }
}
