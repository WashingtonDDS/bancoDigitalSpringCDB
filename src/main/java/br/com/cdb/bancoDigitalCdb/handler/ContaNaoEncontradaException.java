package br.com.cdb.bancoDigitalCdb.handler;

public class ContaNaoEncontradaException extends BusinessException {
    public ContaNaoEncontradaException(String message) {
        super(message);
    }
}
