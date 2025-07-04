package br.com.cdb.bancoDigitalCdb.repository;

import br.com.cdb.bancoDigitalCdb.entity.Cliente;
import br.com.cdb.bancoDigitalCdb.entity.Conta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;

public interface ContaRepository extends JpaRepository<Conta, String> {
    List<Conta> findByClienteId(String clienteId);
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END " +
            "FROM Conta c WHERE c.cliente = :cliente AND TYPE(c) = :tipo")
    boolean existsByClienteAndTipo(
            @Param("cliente") Cliente cliente,
            @Param("tipo") Class<? extends Conta> tipo
    );

}
