package br.com.cdb.bancoDigitalCdb.entity;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@DiscriminatorValue("CC")
public class ContaCorrente extends Conta {

    @Column(precision = 10, scale = 2)
    private BigDecimal taxaDeManutencao;


    @OneToOne(mappedBy = "contaCorrente", cascade = CascadeType.ALL)
    private CartaoDeCredito cartaoDeCredito;


}
