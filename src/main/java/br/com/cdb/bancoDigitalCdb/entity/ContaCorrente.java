package br.com.cdb.bancoDigitalCdb.entity;


import jakarta.persistence.Entity;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ContaCorrente {

    private Cliente cliente;

    private long numeroDaConta;

    private double saldo;

    private double taxaDeManutencao;

    private CartaoDeDebito cartaoDeDebito;

    private CartaoDeCredito cartaoDeCredito;

}
