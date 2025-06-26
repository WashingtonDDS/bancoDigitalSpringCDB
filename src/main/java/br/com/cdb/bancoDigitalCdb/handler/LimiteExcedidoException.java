package br.com.cdb.bancoDigitalCdb.handler;

public class LimiteExcedidoException extends BusinessException {
    public LimiteExcedidoException(String message) {
        super(message);
    }
}
