package br.com.cdb.bancoDigitalCdb.dto;
import br.com.cdb.bancoDigitalCdb.entity.CartaoDeCredito;
import br.com.cdb.bancoDigitalCdb.entity.CartaoDeDebito;
import br.com.cdb.bancoDigitalCdb.entity.TipoCartao;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CartaoDetalhesDTO(
        String id,
        String numero,
        TipoCartao tipo,
        boolean ativo,
        BigDecimal limiteDiario,
        BigDecimal limiteCredito,
        BigDecimal faturaAtual,
        LocalDate dataVencimento,
        String contaId
) {
    public static CartaoDetalhesDTO Debito(CartaoDeDebito cartao) {
        return new CartaoDetalhesDTO(
                cartao.getId(),
                cartao.getNumero(),
                TipoCartao.DEBITO,
                cartao.isAtivoOuDesativo(),
                cartao.getLimiteDiarioTransacao(),
                null,
                null,
                null,
                cartao.getConta().getId()
        );
    }

    public static CartaoDetalhesDTO Credito(CartaoDeCredito cartao) {
        return new CartaoDetalhesDTO(
                cartao.getId(),
                cartao.getNumero(),
                TipoCartao.CREDITO,
                cartao.isAtivoOuDesativo(),
                null,
                cartao.getLimitePreAprovado(),
                cartao.getFaturaAtual(),
                cartao.getDataVencimento(),
                cartao.getContaCorrente().getId()
        );
    }
}