package br.com.cdb.bancoDigitalCdb.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record FaturaResponseDTO(BigDecimal valorAtual,
                                LocalDate dataVencimento,
                                BigDecimal limiteTotal) {
}
