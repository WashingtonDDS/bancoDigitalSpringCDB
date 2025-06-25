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
public class CartaoDeDebito {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "conta_id")
    private Conta conta;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    private String numero;
    private String senha;
    private BigDecimal limiteDiarioTransacao;
    private boolean ativoOuDesativo;
    private double taxaDeManutencao;

    @PrePersist
    @PreUpdate
    public void  validar(){
        if (!conta.getCliente().equals(cliente)){
            throw new IllegalStateException("Conta não pertence ao cliente");
        }
    }

}