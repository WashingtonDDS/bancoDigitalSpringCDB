package br.com.cdb.bancoDigitalCdb.entity;


import jakarta.persistence.*;
import lombok.*;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Endereco {
    private String rua;

    private String numeroDaCasa;

    private String complemento;

    private String bairro;

    private String cidade;

    private String estado;

    private String cep;


}
