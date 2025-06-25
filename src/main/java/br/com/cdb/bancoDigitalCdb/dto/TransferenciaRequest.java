package br.com.cdb.bancoDigitalCdb.dto;

import java.math.BigDecimal;

public record TransferenciaRequest(String destinoContaId, BigDecimal valor) {
}
