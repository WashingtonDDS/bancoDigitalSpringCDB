package br.com.cdb.bancoDigitalCdb.dto;

import java.math.BigDecimal;

public record TransferenciaRequestDTO(String destinoContaId, BigDecimal valor) {
}
