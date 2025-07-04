package br.com.cdb.bancoDigitalCdb.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

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
    @JsonIgnore
    private ContaCorrente contaCorrente;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @OneToMany(mappedBy = "cartao")
    private List<Fatura> faturas;

    @OneToMany(mappedBy = "cartao")
    @JsonManagedReference
    private List<Seguro> seguros;

    @Column(unique = true, length = 16)
    private String numero;

    private String senha;
    private BigDecimal faturaAtual = BigDecimal.ZERO;;
    private BigDecimal limitePreAprovado;

    @Column(name = "data_validade")
    private LocalDate dataValidade;

    @Column(name = "dia_vencimento_fatura")
    private Integer  diaVencimentoFatura;

    @Column(name = "data_proximo_vencimento")
    private LocalDate dataProximoVencimento;

    private boolean ativoOuDesativo;
    private double taxaDeUtilizacao;


    @PrePersist
    @PreUpdate
    public void validar() {
        if (contaCorrente != null && cliente != null) {
            if (!contaCorrente.getCliente().equals(cliente)) {
                throw new IllegalStateException("Conta não pertence ao cliente");
            }
        }
        else if (contaCorrente == null) {
            throw new IllegalStateException("Conta corrente é obrigatória");
        }
        else if (cliente == null) {
            throw new IllegalStateException("Cliente é obrigatório");
        }
    }


}