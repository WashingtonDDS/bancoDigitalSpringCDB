package br.com.cdb.bancoDigitalCdb.repository;


import br.com.cdb.bancoDigitalCdb.entity.Cliente;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente , String> {
    Optional<Cliente> findByEmail(String email);

    @Transactional
    @Modifying
    @Query("update clientes c set c.password = ?2 where c.email = ?1")
    void atualizaSenha(String email, String password);
}
