package br.com.cdb.bancoDigitalCdb.entity;

import jakarta.persistence.Entity;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CartaoDeDebito {
    private Cliente cliente;

    private double limiteDiarioTransacao;

    private boolean ativoOuDesativo;

    private double taxaDeManutencao;

}