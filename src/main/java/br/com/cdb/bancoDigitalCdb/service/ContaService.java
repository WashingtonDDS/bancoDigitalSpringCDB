package br.com.cdb.bancoDigitalCdb.service;

import br.com.cdb.bancoDigitalCdb.dto.*;
import br.com.cdb.bancoDigitalCdb.entity.Cliente;
import br.com.cdb.bancoDigitalCdb.entity.Conta;
import br.com.cdb.bancoDigitalCdb.entity.ContaCorrente;
import br.com.cdb.bancoDigitalCdb.entity.ContaPoupanca;
import br.com.cdb.bancoDigitalCdb.repository.ContaRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

@Service
public class ContaService {
    private final ContaRepository contaRepository;
    private final PixService pixService;

    public ContaService(ContaRepository contaRepository, PixService pixService) {
        this.contaRepository = contaRepository;
        this.pixService = pixService;
    }

    public long gerarNumeroDaConta() {
        return 10000000L + new Random().nextLong(90000000L);
    }

    public ContaPoupanca criarContaPoupanca(Cliente cliente) {
        ContaPoupanca contaPoupanca = new ContaPoupanca();
        contaPoupanca.setCliente(cliente);
        contaPoupanca.setNumeroDaConta(gerarNumeroDaConta());
        contaPoupanca.setSaldo(BigDecimal.ZERO);
        contaPoupanca.setRendimento(0.0);
        return (ContaPoupanca) contaRepository.save(contaPoupanca);
    }

    public ContaCorrente criarContaCorrente(Cliente cliente) {
        ContaCorrente contaCorrente = new ContaCorrente();
        contaCorrente.setCliente(cliente);
        contaCorrente.setNumeroDaConta(gerarNumeroDaConta());
        contaCorrente.setSaldo(BigDecimal.ZERO);
        contaCorrente.setTaxaDeManutencao(0.0);
        return (ContaCorrente) contaRepository.save(contaCorrente);
    }

    public List<Conta> listarTodasContas() {
        return contaRepository.findAll();
    }

    public Conta detalharConta(String contaId) {
        return contaRepository.findById(contaId)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada"));
    }
    @Transactional
    public SaldoResponse consultarSaldo(String contaId) {
        Conta conta = contaRepository.findById(contaId)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada"));
        return new SaldoResponse(conta.getSaldo());
    }
    @Transactional
    public void realizarDeposito(String contaId, DepositoRequest request){
        Conta conta = contaRepository.findById(contaId)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada"));
        if (request.valor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Valor do depósito deve ser positivo");
        }
        conta.setSaldo(conta.getSaldo().add(request.valor()));
        contaRepository.save(conta);
    }
    @Transactional
    public void realizarSaque(String contaId, SaqueRequest request){
        Conta conta = contaRepository.findById(contaId)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada"));
        if (request.valor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Valor do saque deve ser positivo");
        }
        if (conta.getSaldo().compareTo(request.valor()) < 0) {
            throw new RuntimeException("Saldo insuficiente para saque");
        }
        conta.setSaldo(conta.getSaldo().subtract(request.valor()));
        contaRepository.save(conta);
    }

    @Transactional
    public void transferir(String contaOrigemId, TransferenciaRequest request){
        Conta origem = contaRepository.findById(contaOrigemId)
                .orElseThrow(() -> new RuntimeException("Conta de origem não encontrada"));
        Conta destino = contaRepository.findById(request.destinoContaId())
                .orElseThrow(() -> new RuntimeException("Conta de destino não encontrada"));

        if (origem.getSaldo().compareTo(request.valor()) < 0) {
            throw new RuntimeException("Saldo insuficiente para transferência");
        }

        origem.setSaldo(origem.getSaldo().subtract(request.valor()));
        destino.setSaldo(destino.getSaldo().add(request.valor()));

        contaRepository.save(origem);
        contaRepository.save(destino);
    }

    @Transactional
    public void fazerPix(String contaId, PixRequest request){
        Conta conta = contaRepository.findById(contaId)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada"));
        if (conta.getSaldo().compareTo(request.valor()) < 0) {
            throw new RuntimeException("Saldo insuficiente para realizar o PIX");
        }
        pixService.processarPix(conta,request.chaveDestinoCpf(),request.valor());
        conta.setSaldo(conta.getSaldo().subtract(request.valor()));
        contaRepository.save(conta);


    }
}

