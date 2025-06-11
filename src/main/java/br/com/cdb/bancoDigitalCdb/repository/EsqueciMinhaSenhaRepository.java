package br.com.cdb.bancoDigitalCdb.repository;

import br.com.cdb.bancoDigitalCdb.entity.Cliente;
import br.com.cdb.bancoDigitalCdb.entity.EsqueciMinhaSenha;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface EsqueciMinhaSenhaRepository extends JpaRepository<EsqueciMinhaSenha, String> {

    Optional<EsqueciMinhaSenha> findByOtpCliente(Integer otp, Cliente cliente);
}
