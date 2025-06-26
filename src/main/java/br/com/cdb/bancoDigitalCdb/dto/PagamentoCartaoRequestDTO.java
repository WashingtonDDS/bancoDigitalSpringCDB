package br.com.cdb.bancoDigitalCdb.dto;

import java.math.BigDecimal;

public record PagamentoCartaoRequestDTO(String senha, BigDecimal valor) {
}
