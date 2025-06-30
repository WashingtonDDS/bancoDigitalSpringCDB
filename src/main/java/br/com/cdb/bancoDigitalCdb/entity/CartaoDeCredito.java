package br.com.cdb.bancoDigitalCdb.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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

    @OneToMany(mappedBy = "cartao")
    private List<Fatura> faturas;

    private String numero;
    private String senha;
    private BigDecimal faturaAtual = BigDecimal.ZERO;;
    private BigDecimal limitePreAprovado;
    private LocalDate dataVencimento;
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