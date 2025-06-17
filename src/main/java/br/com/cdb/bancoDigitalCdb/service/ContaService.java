package br.com.cdb.bancoDigitalCdb.service;

import br.com.cdb.bancoDigitalCdb.entity.Cliente;
import br.com.cdb.bancoDigitalCdb.entity.Conta;
import br.com.cdb.bancoDigitalCdb.entity.ContaCorrente;
import br.com.cdb.bancoDigitalCdb.entity.ContaPoupanca;
import br.com.cdb.bancoDigitalCdb.repository.ContaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class ContaService {
    private final ContaRepository contaRepository;

    public ContaService(ContaRepository contaRepository) {
        this.contaRepository = contaRepository;
    }

    public long gerarNumeroDaConta() {
        return 10000000L + new Random().nextLong(90000000L);
    }

    public ContaPoupanca criarContaPoupanca(Cliente cliente) {
        ContaPoupanca contaPoupanca = new ContaPoupanca();
        contaPoupanca.setCliente(cliente);
        contaPoupanca.setNumeroDaConta(gerarNumeroDaConta());
        contaPoupanca.setSaldo(0.0);
        contaPoupanca.setRendimento(0.0);
        return (ContaPoupanca) contaRepository.save(contaPoupanca);
    }

    public ContaCorrente criarContaCorrente(Cliente cliente) {
        ContaCorrente contaCorrente = new ContaCorrente();
        contaCorrente.setCliente(cliente);
        contaCorrente.setNumeroDaConta(gerarNumeroDaConta());
        contaCorrente.setSaldo(0.0);
        contaCorrente.setTaxaDeManutencao(0.0);
        return (ContaCorrente) contaRepository.save(contaCorrente);
    }

    public List<Conta> listarTodasContas() {
        return contaRepository.findAll();
    }
}

