package br.com.cdb.bancoDigitalCdb.dto;

import br.com.cdb.bancoDigitalCdb.entity.TipoSeguro;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SeguroDetalhesDTO(String id,
                                String numeroApolice, // Incluído número da apólice
                                TipoSeguro tipo,
                                LocalDate dataContratacao,
                                LocalDate dataCancelamento,
                                BigDecimal valorMensal,
                                String cartaoId,
                                BigDecimal valorCobertura,
                                String tipoCliente) {
}
