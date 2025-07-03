package br.com.cdb.bancoDigitalCdb.repository;

import br.com.cdb.bancoDigitalCdb.entity.CartaoDeCredito;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CartaoCreditoRepository extends JpaRepository<CartaoDeCredito, String> {


    @EntityGraph(attributePaths = {"contaCorrente", "contaCorrente.cliente"})
    @Query("SELECT c FROM CartaoDeCredito c WHERE c.id = :cartaoId")
    Optional<CartaoDeCredito> findCartaoComCliente(@Param("cartaoId") String cartaoId);

    @EntityGraph(attributePaths = {"contaCorrente.cliente"})
    Optional<CartaoDeCredito> findById(String id);

    boolean existsByIdAndContaCorrente_Cliente_Email(String cartaoId, String email);
}
