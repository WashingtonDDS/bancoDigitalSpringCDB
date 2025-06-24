package br.com.cdb.bancoDigitalCdb.dto;

import br.com.cdb.bancoDigitalCdb.entity.ClienteRole;
import br.com.cdb.bancoDigitalCdb.entity.Endereco;
import br.com.cdb.bancoDigitalCdb.entity.TipoCliente;
import br.com.cdb.bancoDigitalCdb.entity.TipoDeConta;

import java.time.LocalDate;

public record RegisterRequestDTO(String nome, String email, String password, String cpf, LocalDate dataDeNascimento, Endereco endereco, TipoCliente tipoCliente, ClienteRole role, TipoDeConta tipoDeConta) {
}
