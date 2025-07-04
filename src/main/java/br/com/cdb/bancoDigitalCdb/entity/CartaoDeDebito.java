package br.com.cdb.bancoDigitalCdb.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Random;

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

    @Column(name = "data_validade")
    private LocalDate dataValidade;


    @Column(unique = true, length = 16)
    private String numero;

    private String senha;
    private BigDecimal limiteDiarioTransacao;

    @Column(precision = 10, scale = 2)
    private BigDecimal gastoDiarioAtual = BigDecimal.ZERO;

    private LocalDate dataUltimaTransacao;

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