package br.com.cdb.bancoDigitalCdb.dto;

import java.math.BigDecimal;

public record SeguroDisponivelDTO(String tipo,
                                  String nome,
                                  String descricao,
                                  BigDecimal valorMensal) {
}
