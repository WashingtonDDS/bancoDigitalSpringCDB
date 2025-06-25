package br.com.cdb.bancoDigitalCdb.dto;

import br.com.cdb.bancoDigitalCdb.entity.TipoCartao;

import java.math.BigDecimal;

public record CartaoRequestDTO(String contaId,
                               TipoCartao tipo,
                               String senha,
                               BigDecimal limiteDiario,
                               BigDecimal limiteCredito) {
}
