package br.com.cdb.bancoDigitalCdb.entity;

public class CartaoDeCredito {
    private ContaCorrente conta;
    private final Cliente cliente;
    private final double limitePreAprovado;
    private String dataVencimento;
    private boolean ativoOuDesativo;
    private double taxaDeUtilizacao;

}