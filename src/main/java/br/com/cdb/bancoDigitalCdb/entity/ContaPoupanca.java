package br.com.cdb.bancoDigitalCdb.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ContaPoupanca {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @Column(unique = true)
    private long numeroDaConta;

    private double saldo;
    private double rendimento;

    @OneToOne(mappedBy = "contaPoupanca", cascade = CascadeType.ALL)
    private CartaoDeDebito cartaoDeDebito;


}
