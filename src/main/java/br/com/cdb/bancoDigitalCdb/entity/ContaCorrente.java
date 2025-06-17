package br.com.cdb.bancoDigitalCdb.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ContaCorrente extends Conta {


    private double taxaDeManutencao;

    @OneToOne(mappedBy = "contaCorrente", cascade = CascadeType.ALL)
    private CartaoDeDebito cartaoDeDebito;

    @OneToOne(mappedBy = "contaCorrente", cascade = CascadeType.ALL)
    private CartaoDeCredito cartaoDeCredito;


}
