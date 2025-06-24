package br.com.cdb.bancoDigitalCdb.dto;

import br.com.cdb.bancoDigitalCdb.entity.TipoDeConta;

import java.math.BigDecimal;

public record ContaRequestDTO (String clienteId, TipoDeConta tipo, BigDecimal limiteChequeEspecial,BigDecimal taxaRendimento) {
}
