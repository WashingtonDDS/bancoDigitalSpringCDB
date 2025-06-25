package br.com.cdb.bancoDigitalCdb.repository;

import br.com.cdb.bancoDigitalCdb.entity.CartaoDeCredito;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartaoCreditoRepository extends JpaRepository<CartaoDeCredito, String> {
}
