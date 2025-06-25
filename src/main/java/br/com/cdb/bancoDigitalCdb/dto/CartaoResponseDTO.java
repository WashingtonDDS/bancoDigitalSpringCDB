package br.com.cdb.bancoDigitalCdb.dto;

import br.com.cdb.bancoDigitalCdb.entity.CartaoDeCredito;
import br.com.cdb.bancoDigitalCdb.entity.CartaoDeDebito;
import br.com.cdb.bancoDigitalCdb.entity.TipoCartao;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CartaoResponseDTO(String id,
                                String numero,
                                TipoCartao tipo,
                                BigDecimal limiteDiario,
                                BigDecimal limiteCredito,
                                LocalDate dataVencimento) {
    public CartaoResponseDTO(CartaoDeDebito cartaoDeDebito){
        this(cartaoDeDebito.getId(), cartaoDeDebito.getNumero(), TipoCartao.DEBITO, cartaoDeDebito.getLimiteDiarioTransacao(), null, null);
    }
    public CartaoResponseDTO(CartaoDeCredito cartao) {
        this(cartao.getId(), cartao.getNumero(), TipoCartao.CREDITO, null, cartao.getLimitePreAprovado(), cartao.getDataVencimento());
    }
}

