package br.com.cdb.bancoDigitalCdb.repository;

import br.com.cdb.bancoDigitalCdb.entity.CartaoDeDebito;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartaoDebitoRepository extends JpaRepository <CartaoDeDebito, String>{
}
