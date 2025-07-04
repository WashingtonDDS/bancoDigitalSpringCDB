package br.com.cdb.bancoDigitalCdb.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record FaturaResponseDTO(
        BigDecimal valorTotal,
        LocalDate dataVencimento,
        BigDecimal limiteTotal,
        BigDecimal limiteDisponivel,
        List<PagamentoFaturaDTO> historicoPagamentos,
        BigDecimal valorTaxas,
        BigDecimal valorSeguros
) {}
