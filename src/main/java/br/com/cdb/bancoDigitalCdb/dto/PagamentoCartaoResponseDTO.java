package br.com.cdb.bancoDigitalCdb.dto;

import java.math.BigDecimal;

public record PagamentoCartaoResponseDTO(
        String mensagem,
        BigDecimal valorPagamento,
        BigDecimal limiteTotal,
        BigDecimal limiteDisponivel
) {}
