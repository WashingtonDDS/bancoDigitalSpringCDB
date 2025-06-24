package br.com.cdb.bancoDigitalCdb.dto;

import br.com.cdb.bancoDigitalCdb.entity.TipoDeConta;

public record ContaResponseDTO(String id, long numero, TipoDeConta tipo, double saldo, String clienteId) {
}
