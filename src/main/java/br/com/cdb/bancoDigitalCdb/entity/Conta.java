package br.com.cdb.bancoDigitalCdb.entity;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


@Entity
@Setter
@Getter
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "tipo_conta")
public abstract class Conta {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true)
    private long numeroDaConta;

    private BigDecimal saldo;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;
}
