package br.com.cdb.bancoDigitalCdb.handler;

public class OperacaoNaoPermitidaException extends BusinessException {
    public OperacaoNaoPermitidaException(String message) {
        super(message);
    }
}
