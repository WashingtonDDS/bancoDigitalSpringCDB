package br.com.cdb.bancoDigitalCdb.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ContaCorrente {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @Column(unique = true)
    private long numeroDaConta;

    private double saldo;
    private double taxaDeManutencao;

    @OneToOne(mappedBy = "contaCorrente", cascade = CascadeType.ALL)
    private CartaoDeDebito cartaoDeDebito;

    @OneToOne(mappedBy = "contaCorrente", cascade = CascadeType.ALL)
    private CartaoDeCredito cartaoDeCredito;


}
