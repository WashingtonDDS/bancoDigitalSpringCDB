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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class CartaoService {
    private final CartaoDebitoRepository cartaoDebitoRepository;
    private final CartaoCreditoRepository cartaoCreditoRepository;
    private final ContaRepository contaRepository;
    private final FaturaRepository faturaRepository;
    private final PasswordEncoder passwordEncoder;
    private final SeguroService seguroService;

    public CartaoService(CartaoDebitoRepository cartaoDebitoRepository, CartaoCreditoRepository cartaoCreditoRepository, ContaRepository contaRepository, FaturaRepository faturaRepository, PasswordEncoder passwordEncoder, SeguroService seguroService) {
        this.cartaoDebitoRepository = cartaoDebitoRepository;
        this.cartaoCreditoRepository = cartaoCreditoRepository;
        this.contaRepository = contaRepository;
        this.faturaRepository = faturaRepository;
        this.passwordEncoder = passwordEncoder;
        this.seguroService = seguroService;
    }

    @Transactional
    public CartaoResponseDTO emitirCartao(CartaoRequestDTO request) {
        Conta conta = contaRepository.findById(request.contaId())
                .orElseThrow(() -> new ContaNaoEncontradaException("Conta não encontrada"));

        if (request.tipo() == TipoCartao.DEBITO) {
            CartaoDeDebito cartao = new CartaoDeDebito();
            cartao.setNumero(gerarNumeroCartao());
            cartao.setSenha(passwordEncoder.encode(validarSenhaCartao(request.senha())));
            cartao.setLimiteDiarioTransacao(request.limiteDiario());
            cartao.setConta(conta);
            cartaoDebitoRepository.save(cartao);
            return new CartaoResponseDTO(cartao);
        } else {

            if (!(conta instanceof ContaCorrente)) {
                throw new BusinessException("Cartão de crédito só pode ser associado a conta corrente");
            }

            ContaCorrente contaCorrente = (ContaCorrente) conta;
            Cliente cliente = contaCorrente.getCliente();

            CartaoDeCredito cartao = new CartaoDeCredito();
            cartao.setNumero(gerarNumeroCartao());
            cartao.setSenha(passwordEncoder.encode(validarSenhaCartao(request.senha())));


            BigDecimal limiteCredito = calcularLimitePorCliente(cliente);
            cartao.setLimitePreAprovado(limiteCredito);
            cartao.setFaturaAtual(BigDecimal.ZERO);

            cartao.setDataVencimento(LocalDate.now().plusYears(3));
            cartao.setContaCorrente(contaCorrente);
            cartao.setCliente(cliente);

            cartaoCreditoRepository.save(cartao);
            criarSeguroFraudeAutomatico(cartao);
            return new CartaoResponseDTO(cartao);
        }
    }
    private void criarSeguroFraudeAutomatico(CartaoDeCredito cartao) {
        ContratarSeguroRequestDTO request = new ContratarSeguroRequestDTO(
                cartao.getId(),
                TipoSeguro.FRAUDE
        );
        seguroService.contratarSeguro(request);
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
        if (senha == null || !senha.matches("\\d{4}")) {
            throw new BusinessException("Senha deve conter exatamente 4 dígitos numéricos");
        }
        return senha;
    }
    private String gerarNumeroCartao(){
        return "5" + String.format("%015d", new Random().nextLong() % 1000000000000000L);
    }

    @Transactional
    public CartaoDetalhesDTO detalhaCartao(String cartaoId){
        if (cartaoId.startsWith("CD")){
            CartaoDeDebito cartao = cartaoDebitoRepository.findById(cartaoId)
                    .orElseThrow(() -> new CartaoNaoEncontradaException("Cartão de débito não encontrado"));
            return CartaoDetalhesDTO.Debito(cartao);
        } else {
            CartaoDeCredito cartao = cartaoCreditoRepository.findById(cartaoId)
                    .orElseThrow(() -> new CartaoNaoEncontradaException("Cartão de crédito não encontrado"));
            return CartaoDetalhesDTO.Credito(cartao);
        }
    }

    @Transactional
    public void realizarPagamentoComCartao(String cartaoId, PagamentoCartaoRequestDTO request){
        if (cartaoId.startsWith("CD")){
            CartaoDeDebito cartao = cartaoDebitoRepository.findById(cartaoId)
                    .orElseThrow(()-> new CartaoNaoEncontradaException("Cartão de débito não encontrado"));

            LocalDate hoje = LocalDate.now();
            if (cartao.getDataUltimaTransacao() == null || !cartao.getDataUltimaTransacao().equals(hoje)) {
                cartao.setGastoDiarioAtual(BigDecimal.ZERO);
                cartao.setDataUltimaTransacao(hoje);
            }
            BigDecimal novoGastoDiario = cartao.getGastoDiarioAtual().add(request.valor());

            if (novoGastoDiario.compareTo(cartao.getLimiteDiarioTransacao()) > 0) {
                throw new LimiteExcedidoException("Limite diário excedido");
            }
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
            cartao.setGastoDiarioAtual(novoGastoDiario);

            conta.setSaldo(conta.getSaldo().subtract(request.valor()));
            contaRepository.save(conta);
        } else {
            CartaoDeCredito cartao = cartaoCreditoRepository.findById(cartaoId)
                    .orElseThrow(() -> new CartaoNaoEncontradaException("Cartão não encontrado"));
            if (!passwordEncoder.matches(request.senha(), cartao.getSenha())){
                throw new SenhaIncorretaException("Senha incorreta");
            }
            BigDecimal limiteDisponivel = cartao.getLimitePreAprovado().subtract(cartao.getFaturaAtual());

            if (cartao.getFaturaAtual().compareTo(cartao.getLimitePreAprovado()) > 0) {
                throw new LimiteExcedidoException("Fatura excedeu o limite do cartão");
            }

            if (limiteDisponivel.compareTo(request.valor()) < 0) {
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
        BigDecimal limiteCredito = cartao.getLimitePreAprovado();

        BigDecimal taxaAplicada = BigDecimal.ZERO;

        if (aplicarTaxaUtilizacao(faturaAtual,limiteCredito)){
            taxaAplicada = calcularTaxaUtilizacao(faturaAtual);
            faturaAtual = faturaAtual.add(taxaAplicada);
            cartao.setFaturaAtual(faturaAtual);
        }

        if (valorPagamento.compareTo(faturaAtual) > 0) {
            throw new PagamentoFaturaException("Valor do pagamento não pode ser maior que o total da fatura");
        }

        if (conta.getSaldo().compareTo(valorPagamento) < 0){
            throw new SaldoInsuficienteException("Saldo insuficiente para pagar a fatura");
        }

        conta .setSaldo(conta.getSaldo().subtract(valorPagamento));

        BigDecimal novaFatura = faturaAtual.subtract(valorPagamento);
        cartao.setFaturaAtual(novaFatura);

        contaRepository.save(conta);
        cartaoCreditoRepository.save(cartao);

        Fatura fatura = new Fatura();
        fatura.setCartao(cartao);
        fatura.setValorPago(valorPagamento);
        fatura.setDataPagamento(LocalDate.now());
        fatura.setTotalTaxas(taxaAplicada);

        faturaRepository.save(fatura);
    }

    private boolean aplicarTaxaUtilizacao(BigDecimal faturaAtual, BigDecimal limiteCredito) {
        BigDecimal limite80 = limiteCredito.multiply(BigDecimal.valueOf(0.8));
        return faturaAtual.compareTo(limite80) > 0;
    }
    private BigDecimal calcularTaxaUtilizacao(BigDecimal faturaAtual) {
        return faturaAtual.multiply(BigDecimal.valueOf(0.05));
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

    @Transactional
    public void alterarSenha(String cartaoId, AlterarSenhaRequestDTO request) {
        if (cartaoId.startsWith("CD")) {
            CartaoDeDebito cartao = cartaoDebitoRepository.findById(cartaoId)
                    .orElseThrow(() -> new CartaoNaoEncontradaException("Cartão de débito não encontrado"));

            if (!passwordEncoder.matches(request.senhaAtual(), cartao.getSenha())) {
                throw new SenhaIncorretaException("Senha atual incorreta");
            }

            validarSenhaCartao(request.novaSenha());

            cartao.setSenha(passwordEncoder.encode(request.novaSenha()));
            cartaoDebitoRepository.save(cartao);
        } else {
            CartaoDeCredito cartao = cartaoCreditoRepository.findById(cartaoId)
                    .orElseThrow(() -> new CartaoNaoEncontradaException("Cartão de crédito não encontrado"));

            if (!passwordEncoder.matches(request.senhaAtual(), cartao.getSenha())) {
                throw new SenhaIncorretaException("Senha atual incorreta");
            }

            validarSenhaCartao(request.novaSenha());

            cartao.setSenha(passwordEncoder.encode(request.novaSenha()));
            cartaoCreditoRepository.save(cartao);
        }
    }

    @Transactional
    public FaturaResponseDTO consultarFatura(String cartaoId) {
        CartaoDeCredito cartao = cartaoCreditoRepository.findById(cartaoId)
                .orElseThrow(() -> new CartaoNaoEncontradaException("Cartão de crédito não encontrado"));

        List<PagamentoFaturaDTO> historico = new ArrayList<>();

        if (cartao.getFaturas() != null){
            historico = cartao.getFaturas().stream()
                    .map(fatura -> new PagamentoFaturaDTO(
                            fatura.getValorPago(),
                            fatura.getDataPagamento(),
                            fatura.getCartao().getNumero()
                    ))
                    .collect(Collectors.toList());
        }

        return new FaturaResponseDTO(
                cartao.getFaturaAtual(),
                cartao.getDataVencimento(),
                cartao.getLimitePreAprovado(),
                historico
                );
    }




}
