package br.com.cdb.bancoDigitalCdb.repository;


import br.com.cdb.bancoDigitalCdb.entity.Fatura;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FaturaRepository extends JpaRepository<Fatura, String> {
    List<Fatura> findByCartaoId(String cartaoId);

}
