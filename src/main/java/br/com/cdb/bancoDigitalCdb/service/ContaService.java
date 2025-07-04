package br.com.cdb.bancoDigitalCdb.service;

import br.com.cdb.bancoDigitalCdb.dto.*;
import br.com.cdb.bancoDigitalCdb.entity.Cliente;
import br.com.cdb.bancoDigitalCdb.entity.Conta;
import br.com.cdb.bancoDigitalCdb.entity.ContaCorrente;
import br.com.cdb.bancoDigitalCdb.entity.ContaPoupanca;
import br.com.cdb.bancoDigitalCdb.handler.BusinessException;
import br.com.cdb.bancoDigitalCdb.handler.ContaNaoEncontradaException;
import br.com.cdb.bancoDigitalCdb.handler.SaldoInsuficienteException;
import br.com.cdb.bancoDigitalCdb.repository.ContaRepository;
import ch.obermuhlner.math.big.BigDecimalMath;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
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
        if (contaRepository.existsByClienteAndTipo(cliente, ContaPoupanca.class)) {
            throw new BusinessException("Cliente já tem conta poupança");
        }
        ContaPoupanca contaPoupanca = new ContaPoupanca();
        contaPoupanca.setCliente(cliente);
        contaPoupanca.setNumeroDaConta(gerarNumeroDaConta());
        contaPoupanca.setSaldo(BigDecimal.ZERO);
        contaPoupanca.setRendimento(BigDecimal.ZERO);
        return (ContaPoupanca) contaRepository.save(contaPoupanca);
    }

    public ContaCorrente criarContaCorrente(Cliente cliente) {
        if (contaRepository.existsByClienteAndTipo(cliente, ContaCorrente.class)) {
            throw new BusinessException("Cliente já tem conta corrente");
        }
        ContaCorrente contaCorrente = new ContaCorrente();
        contaCorrente.setCliente(cliente);
        contaCorrente.setNumeroDaConta(gerarNumeroDaConta());
        contaCorrente.setSaldo(BigDecimal.ZERO);
        contaCorrente.setTaxaDeManutencao(BigDecimal.ZERO);
        return (ContaCorrente) contaRepository.save(contaCorrente);
    }

    public List<Conta> listarTodasContas() {
        return contaRepository.findAll();
    }

    public Conta detalharConta(String contaId) {
        return contaRepository.findById(contaId)
                .orElseThrow(() -> new ContaNaoEncontradaException("Conta não encontrada"));
    }
    @Transactional
    public SaldoResponseDTO consultarSaldo(String contaId) {
        Conta conta = contaRepository.findById(contaId)
                .orElseThrow(() -> new ContaNaoEncontradaException("Conta não encontrada"));
        return new SaldoResponseDTO(conta.getSaldo());
    }
    @Transactional
    public void realizarDeposito(String contaId, DepositoRequestDTO request){
        Conta conta = contaRepository.findById(contaId)
                .orElseThrow(() -> new ContaNaoEncontradaException("Conta não encontrada"));
        if (request.valor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new SaldoInsuficienteException("Valor do depósito deve ser positivo");
        }
        conta.setSaldo(conta.getSaldo().add(request.valor()));
        contaRepository.save(conta);
    }
    @Transactional
    public void realizarSaque(String contaId, SaqueRequestDTO request){
        Conta conta = contaRepository.findById(contaId)
                .orElseThrow(() -> new ContaNaoEncontradaException("Conta não encontrada"));
        if (request.valor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new SaldoInsuficienteException("Valor do saque deve ser positivo");
        }
        if (conta.getSaldo().compareTo(request.valor()) < 0) {
            throw new SaldoInsuficienteException("Saldo insuficiente para saque");
        }
        conta.setSaldo(conta.getSaldo().subtract(request.valor()));
        contaRepository.save(conta);
    }

    @Transactional
    public void transferir(String contaOrigemId, TransferenciaRequestDTO request){
        Conta origem = contaRepository.findById(contaOrigemId)
                .orElseThrow(() -> new ContaNaoEncontradaException("Conta de origem não encontrada"));
        Conta destino = contaRepository.findById(request.destinoContaId())
                .orElseThrow(() -> new ContaNaoEncontradaException("Conta de destino não encontrada"));

        if (origem.getSaldo().compareTo(request.valor()) < 0) {
            throw new SaldoInsuficienteException("Saldo insuficiente para transferência");
        }

        origem.setSaldo(origem.getSaldo().subtract(request.valor()));
        destino.setSaldo(destino.getSaldo().add(request.valor()));

        contaRepository.save(origem);
        contaRepository.save(destino);
    }

    @Transactional
    public void fazerPix(String contaId, PixRequestDTO request){
        Conta conta = contaRepository.findById(contaId)
                .orElseThrow(() -> new ContaNaoEncontradaException("Conta não encontrada"));
        if (conta.getSaldo().compareTo(request.valor()) < 0) {
            throw new SaldoInsuficienteException("Saldo insuficiente para realizar o PIX");
        }
        pixService.processarPix(conta,request.chaveDestinoCpf(),request.valor());
    }

    @Transactional
    public void aplicarTaxaManutencao(String contaId){
        ContaCorrente conta = (ContaCorrente) contaRepository.findById(contaId)
                .orElseThrow(() -> new ContaNaoEncontradaException("Conta corrente não encontrada"));

        Cliente cliente = conta.getCliente();
        if (cliente == null) {
            throw new ContaNaoEncontradaException("Cliente não encontrado para a conta corrente");
        }

        BigDecimal taxa;
        switch (cliente.getTipoCliente()){
            case COMUM:
                taxa = BigDecimal.valueOf(12.0);
                break;
            case SUPER:
                taxa = BigDecimal.valueOf(8.0);
                break;
            case PREMIUM:
                taxa = BigDecimal.ZERO;
                break;
            default:
                throw new BusinessException("Tipo de cliente desconhecido : " + cliente.getTipoCliente());
        }

        if (conta.getSaldo().compareTo(taxa)< 0){
            throw new SaldoInsuficienteException("Saldo insuficiente para aplicar a taxa de manutenção");
        }

        conta.setTaxaDeManutencao(taxa);
        conta.setSaldo(conta.getSaldo().add(taxa));
        conta.setSaldo(conta.getSaldo().subtract(taxa));
        contaRepository.save(conta);
    }

    @Transactional
    public void aplicarRendimento(String contaId){
        ContaPoupanca conta = (ContaPoupanca) contaRepository.findById(contaId)
                .orElseThrow(()-> new ContaNaoEncontradaException("Conta poupança não encontrada"));

        Cliente cliente = conta.getCliente();
        if (cliente == null) {
            throw new ContaNaoEncontradaException("Cliente não encontrado para a conta poupança");
        }

        BigDecimal taxaAnual;
        switch (cliente.getTipoCliente()) {
            case COMUM:
                taxaAnual = BigDecimal.valueOf(0.005);
                break;
            case SUPER:
                taxaAnual = BigDecimal.valueOf(0.007);
                break;
            case PREMIUM:
                taxaAnual = BigDecimal.valueOf(0.009);
                break;
            default:
                throw new BusinessException("Tipo de cliente desconhecido: " + cliente.getTipoCliente());
        }
        BigDecimal taxaMensal = calcularTaxaMensalEquivalente(taxaAnual);

        BigDecimal rendimento = conta.getSaldo().multiply(taxaMensal);
        rendimento = rendimento.setScale(2, RoundingMode.HALF_UP);

        conta.setRendimento(rendimento);
        conta.setSaldo(conta.getSaldo().add(rendimento));

        conta.setSaldo(conta.getSaldo().add(rendimento));
        contaRepository.save(conta);
    }
    private BigDecimal calcularTaxaMensalEquivalente(BigDecimal taxaAnual) {
        BigDecimal um = BigDecimal.ONE;
        BigDecimal base = um.add(taxaAnual);

        BigDecimal expoente = BigDecimal.ONE.divide(new BigDecimal(12), 10, RoundingMode.HALF_UP);

        return BigDecimalMath.pow(base, expoente, MathContext.DECIMAL128).subtract(um);
    }
}

