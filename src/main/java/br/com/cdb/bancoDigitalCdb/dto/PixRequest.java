package br.com.cdb.bancoDigitalCdb.dto;

import java.math.BigDecimal;

public record PixRequest(String chaveDestinoCpf, BigDecimal valor) {
}
