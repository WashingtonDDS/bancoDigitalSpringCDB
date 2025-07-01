package br.com.cdb.bancoDigitalCdb.dto;

import br.com.cdb.bancoDigitalCdb.entity.TipoSeguro;

public record ContratarSeguroRequestDTO(String cartaoId,
                                        TipoSeguro tipo) {
}
