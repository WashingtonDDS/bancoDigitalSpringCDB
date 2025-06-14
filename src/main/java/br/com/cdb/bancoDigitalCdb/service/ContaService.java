package br.com.cdb.bancoDigitalCdb.service;

import br.com.cdb.bancoDigitalCdb.entity.Cliente;
import br.com.cdb.bancoDigitalCdb.entity.ContaCorrente;
import br.com.cdb.bancoDigitalCdb.entity.ContaPoupanca;
import br.com.cdb.bancoDigitalCdb.repository.ContaCorrenteRepository;
import br.com.cdb.bancoDigitalCdb.repository.ContaPoupancaRepository;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class ContaService {
    private final ContaPoupancaRepository contaPoupancaRepository;
    private final ContaCorrenteRepository contaCorrenteRepository;

    public ContaService(ContaPoupancaRepository contaPoupancaRepository, ContaCorrenteRepository contaCorrenteRepository) {
        this.contaPoupancaRepository = contaPoupancaRepository;
        this.contaCorrenteRepository = contaCorrenteRepository;
    }


    public long gerarNumeroDaConta(){
        return 10000000L + new Random().nextLong(90000000L);
    }

    public ContaPoupanca criarContaPoupanca(Cliente cliente) {
        ContaPoupanca contaPoupanca = ContaPoupanca.builder()
            .cliente(cliente)
            .numeroDaConta(gerarNumeroDaConta())
            .saldo(0.0)
            .rendimento(0.0)
            .build();
        return contaPoupancaRepository.save(contaPoupanca);
    }

    public ContaCorrente criarContaCorrente(Cliente cliente) {
        ContaCorrente contaCorrente = ContaCorrente.builder()
            .cliente(cliente)
            .numeroDaConta(gerarNumeroDaConta())
            .saldo(0.0)
            .taxaDeManutencao(0.0)
            .build();
        return contaCorrenteRepository.save(contaCorrente);
    }
}
