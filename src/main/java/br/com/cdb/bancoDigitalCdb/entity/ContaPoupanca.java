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
@DiscriminatorValue("CP")
public class ContaPoupanca extends Conta{


    @Column(precision = 10, scale = 2)
    private BigDecimal rendimento;



}
