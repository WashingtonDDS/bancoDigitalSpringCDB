package br.com.cdb.bancoDigitalCdb.dto;

import java.math.BigDecimal;

public record PixRequestDTO(String chaveDestinoCpf, BigDecimal valor) {
}
