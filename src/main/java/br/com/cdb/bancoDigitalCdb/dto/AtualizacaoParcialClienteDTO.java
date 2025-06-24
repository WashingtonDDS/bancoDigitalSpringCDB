package br.com.cdb.bancoDigitalCdb.dto;

import br.com.cdb.bancoDigitalCdb.entity.Endereco;
import br.com.cdb.bancoDigitalCdb.entity.TipoCliente;

import java.time.LocalDate;

public record AtualizacaoParcialClienteDTO(String nome, String email, LocalDate dataDeNascimento, Endereco endereco, TipoCliente tipoCliente) {
}
