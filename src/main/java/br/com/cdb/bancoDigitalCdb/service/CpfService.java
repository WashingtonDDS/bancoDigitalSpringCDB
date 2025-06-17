package br.com.cdb.bancoDigitalCdb.service;

import org.springframework.stereotype.Service;

@Service
public class CpfService {

    public boolean validarCpf(String cpf) {
        cpf = cpf.replaceAll("\\D", "");

        if (cpf.length() != 11 || cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        try {
            int soma1 = 0;
            int soma2 = 0;

            for (int i = 0; i < 9; i++) {
                int digito = cpf.charAt(i) - '0';
                soma1 += digito * (10 - i);
                soma2 += digito * (11 - i);
            }

            int resto1 = soma1 % 11;
            int digito1 = (resto1 < 2) ? 0 : 11 - resto1;

            soma2 += digito1 * 2;
            int resto2 = soma2 % 11;
            int digito2 = (resto2 < 2) ? 0 : 11 - resto2;

            return (cpf.charAt(9) - '0' == digito1) &&
                    (cpf.charAt(10) - '0' == digito2);
        } catch (Exception e) {
            return false;
        }
    }
}