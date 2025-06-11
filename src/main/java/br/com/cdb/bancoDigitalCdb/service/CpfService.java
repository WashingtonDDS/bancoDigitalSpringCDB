package br.com.cdb.bancoDigitalCdb.service;

public class CpfService {
    public static boolean validaCPF(String cpf) {
        cpf = cpf.replaceAll("\\D", "");
        if (cpf.length() != 11 || cpf.matches("(\\d)\\1{10}")) return false;
        try {
            int d1 = 0, d2 = 0;
            for (int i = 0; i < 9; i++) {
                int digit = cpf.charAt(i) - '0';
                d1 += digit * (10 - i);
                d2 += digit * (11 - i);
            }
            d2 += (d1 % 11 < 2 ? 0 : 11 - d1 % 11) * 2;
            return cpf.charAt(9) - '0' == (d1 % 11 < 2 ? 0 : 11 - d1 % 11) &&
                    cpf.charAt(10) - '0' == (d2 % 11 < 2 ? 0 : 11 - d2 % 11);
        } catch (Exception e) {
            return false;
        }
    }
}


