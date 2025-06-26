package br.com.cdb.bancoDigitalCdb.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Fatura {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(precision = 10, scale = 2)
    private BigDecimal totalTaxas = BigDecimal.ZERO;

    private BigDecimal valorPago;
    private LocalDate dataPagamento;

    @ManyToOne
    @JoinColumn(name = "cartao_id")
    private CartaoDeCredito cartao;


    public void adicionarTaxa(BigDecimal valor) {
        this.totalTaxas = this.totalTaxas.add(valor);
    }
}
