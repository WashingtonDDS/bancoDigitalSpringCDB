package br.com.cdb.bancoDigitalCdb.handler;

public class SaldoInsuficienteException extends BusinessException {
    public SaldoInsuficienteException(String message) {
        super(message);
    }
}
