package br.com.cdb.bancoDigitalCdb.repository;

import br.com.cdb.bancoDigitalCdb.entity.Conta;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface ContaRepository extends JpaRepository<Conta, String> {
    List<Conta> findByClienteId(String clienteId);
}
