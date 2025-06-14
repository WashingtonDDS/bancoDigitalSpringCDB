package br.com.cdb.bancoDigitalCdb.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CartaoDeDebito {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    // Relacionamento com ContaCorrente
    @OneToOne
    @JoinColumn(name = "conta_corrente_id")
    private ContaCorrente contaCorrente;


    @OneToOne
    @JoinColumn(name = "conta_poupanca_id")
    private ContaPoupanca contaPoupanca;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    private double limiteDiarioTransacao;
    private boolean ativoOuDesativo;
    private double taxaDeManutencao;

}