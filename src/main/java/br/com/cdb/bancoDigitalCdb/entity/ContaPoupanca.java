package br.com.cdb.bancoDigitalCdb.entity;


import jakarta.persistence.Entity;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ContaPoupanca {

    private Cliente cliente;

    private long numeroDaConta;

    private double saldo;

    private double rendimento;

    private CartaoDeDebito cartaoDeDebito;

}
