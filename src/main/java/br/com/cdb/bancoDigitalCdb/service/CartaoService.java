package br.com.cdb.bancoDigitalCdb.service;


import br.com.cdb.bancoDigitalCdb.dto.*;
import br.com.cdb.bancoDigitalCdb.entity.*;
import br.com.cdb.bancoDigitalCdb.handler.*;
import br.com.cdb.bancoDigitalCdb.repository.CartaoCreditoRepository;
import br.com.cdb.bancoDigitalCdb.repository.CartaoDebitoRepository;
import br.com.cdb.bancoDigitalCdb.repository.ContaRepository;
import br.com.cdb.bancoDigitalCdb.repository.FaturaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Random;

@Service
public class CartaoService {
    private final CartaoDebitoRepository cartaoDebitoRepository;
    private final CartaoCreditoRepository cartaoCreditoRepository;
    private final ContaRepository contaRepository;
    private final FaturaRepository faturaRepository;
    private final PasswordEncoder passwordEncoder;

    public CartaoService(CartaoDebitoRepository cartaoDebitoRepository, CartaoCreditoRepository cartaoCreditoRepository, ContaRepository contaRepository, FaturaRepository faturaRepository, PasswordEncoder passwordEncoder) {
        this.cartaoDebitoRepository = cartaoDebitoRepository;
        this.cartaoCreditoRepository = cartaoCreditoRepository;
        this.contaRepository = contaRepository;
        this.faturaRepository = faturaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public CartaoResponseDTO emitirCartao(CartaoRequestDTO request){
        Conta conta = contaRepository.findById(request.contaId())
                .orElseThrow(() -> new ContaNaoEncontradaException("Conta não encontrada"));
        if (request.tipo()== TipoCartao.DEBITO){
            CartaoDeDebito cartao = new CartaoDeDebito();
            cartao.setNumero(gerarNumeroCartao());
            cartao.setSenha(passwordEncoder.encode(validarSenhaCartao(request.senha())));
            cartao.setLimiteDiarioTransacao(request.limiteDiario());
            cartao.setConta(conta);
            cartaoDebitoRepository.save(cartao);
            return new CartaoResponseDTO(cartao);

        }else {
            CartaoDeCredito cartao = new CartaoDeCredito();
            cartao.setNumero(gerarNumeroCartao());
            cartao.setSenha(passwordEncoder.encode(validarSenhaCartao(request.senha())));

            BigDecimal limiteCredito = calcularLimitePorCliente(conta.getCliente());
            cartao.setLimitePreAprovado(limiteCredito);

            cartao.setDataVencimento(LocalDate.now().plusYears(3));
            cartao.setContaCorrente((ContaCorrente) conta);
            cartaoCreditoRepository.save(cartao);
            return new CartaoResponseDTO(cartao);
        }
    }

    private BigDecimal calcularLimitePorCliente(Cliente cliente){
        return switch (cliente.getTipoCliente()) {
            case COMUM -> new BigDecimal(1000);
            case SUPER -> new BigDecimal(5000);
            case PREMIUM -> new BigDecimal(10000);
            default -> throw new TipoInvalidoException("Tipo de cliente inválido");
        };
    }

    private String validarSenhaCartao(String senha) {
        if (senha == null || senha.length() < 8) {
            throw new BusinessException("Senha deve ter 4  Numeros");
        }
        return senha;
    }
    private String gerarNumeroCartao(){
        return "5" + String.format("%015d", new Random().nextLong() % 1000000000000000L);
    }

    @Transactional
    public void realizarPagamentoComCartao(String cartaoId, PagamentoCartaoRequestDTO request){
        if (cartaoId.startsWith("CD")){
            CartaoDeDebito cartao = cartaoDebitoRepository.findById(cartaoId)
                    .orElseThrow(()-> new CartaoNaoEncontradaException("Cartão de débito não encontrado"));

            if (!passwordEncoder.matches(request.senha(), cartao.getSenha())){
                throw new SenhaIncorretaException("Senha incorreta");
            }
            if (cartao.getLimiteDiarioTransacao().compareTo(request.valor())< 0){
                throw new LimiteExcedidoException("Limite diário excedido");
            }
            Conta conta = cartao.getConta();
            if (conta.getSaldo().compareTo(request.valor()) < 0){
                throw new SaldoInsuficienteException("Saldo insuficiente para realizar o pagamento");
            }
            conta.setSaldo(conta.getSaldo().subtract(request.valor()));
            contaRepository.save(conta);
        } else {
            CartaoDeCredito cartao = cartaoCreditoRepository.findById(cartaoId)
                    .orElseThrow(() -> new CartaoNaoEncontradaException("Cartão não encontrado"));
            if (!passwordEncoder.matches(request.senha(), cartao.getSenha())){
                throw new SenhaIncorretaException("Senha incorreta");
            }

            if (cartao.getLimitePreAprovado().compareTo(request.valor()) < 0) {
                throw new LimiteExcedidoException("Limite indisponível");
            }

            cartao.setFaturaAtual(cartao.getFaturaAtual().add(request.valor()));
            cartaoCreditoRepository.save(cartao);
        }
    }

    @Transactional
    public void pagarFatura(String cartaoId, PagamentoFaturaRequestDTO request){
        CartaoDeCredito cartao = cartaoCreditoRepository.findById(cartaoId)
                .orElseThrow(()-> new CartaoNaoEncontradaException("Cartão de credito não encontrado"));

        Conta conta = contaRepository.findById(request.contaId())
                .orElseThrow(() -> new ContaNaoEncontradaException("Conta não encontrada"));

        BigDecimal valorPagamento = request.valor();
        BigDecimal faturaAtual = cartao.getFaturaAtual();

        if (valorPagamento.compareTo(faturaAtual) > 0) {
            throw new PagamentoFaturaException("Valor do pagamento não pode ser maior que o total da fatura");
        }

        if (conta.getSaldo().compareTo(valorPagamento) < 0){
            throw new SaldoInsuficienteException("Saldo insuficiente para pagar a fatura");
        }

        conta .setSaldo(conta.getSaldo().subtract(valorPagamento));
        cartao.setFaturaAtual(faturaAtual.subtract(valorPagamento));

        contaRepository.save(conta);
        cartaoCreditoRepository.save(cartao);

        Fatura fatura = new Fatura();
        fatura.setCartao(cartao);
        fatura.setValorPago(valorPagamento);
        fatura.setDataPagamento(LocalDate.now());
        faturaRepository.save(fatura);
    }

    @Transactional
    public void alterarLimiteDiario(String cartaoId, AlterarLimiteRequestDTO request){
        CartaoDeDebito cartao = cartaoDebitoRepository.findById(cartaoId)
                .orElseThrow(() -> new CartaoNaoEncontradaException("Cartão de débito não encontrado"));

        if (request.novoLimite().compareTo(BigDecimal.ZERO) <= 0) {
            throw new LimiteException("Limite diário deve ser maior que zero");
        }

        cartao.setLimiteDiarioTransacao(request.novoLimite());
        cartaoDebitoRepository.save(cartao);
    }

    @Transactional
    public void alterarLimiteCredito(String cartaoId, AlterarLimiteRequestDTO request) {
        CartaoDeCredito cartao = cartaoCreditoRepository.findById(cartaoId)
                .orElseThrow(() -> new CartaoNaoEncontradaException("Cartão de crédito não encontrado"));

        if (request.novoLimite().compareTo(BigDecimal.ZERO) <= 0) {
            throw new LimiteException("Limite de crédito deve ser maior que zero");
        }

        cartao.setLimitePreAprovado(request.novoLimite());
        cartaoCreditoRepository.save(cartao);
    }

    @Transactional
    public void alterarStatus(String cartaoId, AlterarStatusRequestDTO request ){
        if (cartaoId.startsWith("CD")){
            CartaoDeDebito cartao = cartaoDebitoRepository.findById(cartaoId)
                    .orElseThrow(() -> new CartaoNaoEncontradaException("Cartão de débito não encontrado"));
            cartao.setAtivoOuDesativo(request.ativo());
            cartaoDebitoRepository.save(cartao);
        } else {
            CartaoDeCredito cartao = cartaoCreditoRepository.findById(cartaoId)
                    .orElseThrow(() -> new CartaoNaoEncontradaException("Cartão de crédito não encontrado"));
            cartao.setAtivoOuDesativo(request.ativo());
            cartaoCreditoRepository.save(cartao);
        }
    }




}
