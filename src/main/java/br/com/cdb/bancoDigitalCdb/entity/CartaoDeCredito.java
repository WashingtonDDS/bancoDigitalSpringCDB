package br.com.cdb.bancoDigitalCdb.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CartaoDeCredito {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;


    @OneToOne
    @JoinColumn(name = "conta_corrente_id")
    private ContaCorrente contaCorrente;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    private double limitePreAprovado;
    private String dataVencimento;
    private boolean ativoOuDesativo;
    private double taxaDeUtilizacao;

}