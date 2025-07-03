package br.com.cdb.bancoDigitalCdb.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ContratarSeguroViagemDTO(
        @NotBlank(message = "ID do cartão não pode estar em branco")
        String cartaoId,

        @NotNull(message = "Flag de seguro não pode ser nula")
        Boolean adquirirSeguroViagem
) {}