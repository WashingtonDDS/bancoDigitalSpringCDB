package br.com.cdb.bancoDigitalCdb.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ContaPoupanca extends Conta{


    private double rendimento;

    @OneToOne(mappedBy = "contaPoupanca", cascade = CascadeType.ALL)
    private CartaoDeDebito cartaoDeDebito;


}
