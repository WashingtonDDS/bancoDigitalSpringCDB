package br.com.cdb.bancoDigitalCdb.repository;


import br.com.cdb.bancoDigitalCdb.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente , String> {
    Optional<Cliente> findByEmail(String email);
}
