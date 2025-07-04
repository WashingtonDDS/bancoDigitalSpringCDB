package br.com.cdb.bancoDigitalCdb.repository;

import br.com.cdb.bancoDigitalCdb.entity.Seguro;
import br.com.cdb.bancoDigitalCdb.entity.TipoSeguro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SeguroRepository extends JpaRepository<Seguro, String> {
    boolean existsAtivoByCartaoIdAndTipo(String cartaoId, TipoSeguro tipo);
    List<Seguro> findByCartaoId(String cartaoId);
    Optional<Seguro> findByNumeroApolice(String numeroApolice);

    @Query("SELECT s FROM Seguro s WHERE s.cartao.id = :cartaoId AND s.dataCancelamento IS NULL")
    List<Seguro> findByCartaoIdAndDataCancelamentoIsNull(String cartaoId);
}