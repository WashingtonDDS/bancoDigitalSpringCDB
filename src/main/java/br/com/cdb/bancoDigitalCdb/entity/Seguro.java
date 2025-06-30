package br.com.cdb.bancoDigitalCdb.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Seguro {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String numeroApolice;

    @Enumerated(EnumType.STRING)
    private TipoSeguro tipo;

    private LocalDate dataContratacao;
    private LocalDate dataCancelamento;

    @Column(precision = 10, scale = 2)
    private BigDecimal valorMensal;

    @ManyToOne
    @JoinColumn(name = "cartao_id")
    private CartaoDeCredito cartao;

    @PrePersist
    protected void gerarNumeroApolice() {
        if (this.numeroApolice == null) {
            this.numeroApolice = "APOL-" +
                    LocalDate.now().getYear() + "-" +
                    UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
    }
}
