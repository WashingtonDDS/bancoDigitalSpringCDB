package br.com.cdb.bancoDigitalCdb.handler;

public class CartaoNaoEncontradaException extends BusinessException {
    public CartaoNaoEncontradaException(String message) {
        super(message);
    }
}
