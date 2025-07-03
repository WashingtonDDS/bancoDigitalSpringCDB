package br.com.cdb.bancoDigitalCdb.dto;

import br.com.cdb.bancoDigitalCdb.entity.TipoSeguro;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ContratarSeguroDTO(
        @NotBlank(message = "ID do cartão é obrigatório")
        @JsonProperty("cartao_id")
        String cartaoId,

        @NotNull(message = "Tipo de seguro é obrigatório")
        TipoSeguro tipo) {


}
