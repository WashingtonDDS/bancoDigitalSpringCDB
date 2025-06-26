package br.com.cdb.bancoDigitalCdb.dto;

import java.math.BigDecimal;

public record PagamentoFaturaRequestDTO(String contaId, BigDecimal valor) {
}
