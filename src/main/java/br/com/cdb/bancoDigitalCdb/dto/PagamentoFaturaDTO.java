package br.com.cdb.bancoDigitalCdb.dto;


import java.math.BigDecimal;
import java.time.LocalDate;

public record PagamentoFaturaDTO(
        BigDecimal valorPago,
        LocalDate dataPagamento
) {}