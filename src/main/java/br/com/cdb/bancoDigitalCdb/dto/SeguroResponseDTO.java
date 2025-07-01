package br.com.cdb.bancoDigitalCdb.dto;

import br.com.cdb.bancoDigitalCdb.entity.TipoSeguro;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SeguroResponseDTO(String id,
                                String numeroApolice,
                                TipoSeguro tipo,
                                LocalDate dataContratacao,
                                LocalDate dataCancelamento,
                                BigDecimal valorMensal,
                                String cartaoId) {
}
