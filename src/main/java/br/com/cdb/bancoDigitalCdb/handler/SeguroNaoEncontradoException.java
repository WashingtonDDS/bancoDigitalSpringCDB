package br.com.cdb.bancoDigitalCdb.handler;

public class SeguroNaoEncontradoException extends BusinessException {
    public SeguroNaoEncontradoException(String message) {
        super(message);
    }
}
