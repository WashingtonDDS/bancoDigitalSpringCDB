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
    @JoinColumn(name = "cliente_id", referencedColumnName = "id")
    private Cliente cliente;

    private long numeroDaConta;

    private double saldo;

    private double rendimento;

    @OneToOne(cascade = CascadeType.ALL)
    private CartaoDeDebito cartaoDeDebito;

}
