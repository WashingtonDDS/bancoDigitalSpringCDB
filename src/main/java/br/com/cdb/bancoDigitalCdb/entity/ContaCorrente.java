package br.com.cdb.bancoDigitalCdb.entity;

public class ContaCorrente implements ContaBancaria {

    private Cliente cliente;
    private long numeroDaConta;
    private double saldo;
    private double taxaDeManutencao;
    private CartaoDeDebito cartaoDeDebito;
    private CartaoDeCredito cartaoDeCredito;

}
