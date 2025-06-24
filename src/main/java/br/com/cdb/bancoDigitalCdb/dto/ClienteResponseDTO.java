package br.com.cdb.bancoDigitalCdb.dto;

import br.com.cdb.bancoDigitalCdb.entity.Cliente;
import br.com.cdb.bancoDigitalCdb.entity.Endereco;

import java.time.LocalDate;

public record ClienteResponseDTO(
        String nome,
        String email,
        String cpfMascarado, // 231.***.**213
        LocalDate dataNascimento,
        Endereco endereco
) {
    public ClienteResponseDTO(Cliente cliente) {
        this(
                cliente.getNome(),
                cliente.getEmail(),
                mascararCpf(cliente.getCpf()),
                cliente.getDataDeNascimento(),
                cliente.getEndereco()
        );
    }

    private static String mascararCpf(String cpf) {
        if (cpf == null || cpf.length() != 11) return "***";
        return cpf.substring(0, 3) + ".***.**" + cpf.substring(9);
    }
}