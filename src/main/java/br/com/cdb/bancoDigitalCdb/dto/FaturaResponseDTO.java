package br.com.cdb.bancoDigitalCdb.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record FaturaResponseDTO(BigDecimal valorAtual,
                                LocalDate dataVencimento,
                                BigDecimal limiteTotal,
 List<PagamentoFaturaDTO>historico) {
}
