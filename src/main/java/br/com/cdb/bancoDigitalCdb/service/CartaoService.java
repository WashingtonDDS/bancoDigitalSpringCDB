package br.com.cdb.bancoDigitalCdb.service;


import br.com.cdb.bancoDigitalCdb.dto.*;
import br.com.cdb.bancoDigitalCdb.entity.*;
import br.com.cdb.bancoDigitalCdb.handler.*;
import br.com.cdb.bancoDigitalCdb.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    private final SeguroRepository seguroRepository;

    public CartaoService(CartaoDebitoRepository cartaoDebitoRepository, CartaoCreditoRepository cartaoCreditoRepository, ContaRepository contaRepository, FaturaRepository faturaRepository, PasswordEncoder passwordEncoder, SeguroService seguroService, SeguroRepository seguroRepository) {
        this.cartaoDebitoRepository = cartaoDebitoRepository;
        this.cartaoCreditoRepository = cartaoCreditoRepository;
        this.contaRepository = contaRepository;
        this.faturaRepository = faturaRepository;
        this.passwordEncoder = passwordEncoder;
        this.seguroService = seguroService;
        this.seguroRepository = seguroRepository;
    }

    @Transactional
    public CartaoResponseDTO emitirCartao(CartaoRequestDTO request) {
        try {
            Conta conta = contaRepository.findById(request.contaId())
                    .orElseThrow(() -> new ContaNaoEncontradaException("Conta não encontrada"));

            if (request.tipo() == TipoCartao.DEBITO) {
                CartaoDeDebito cartao = new CartaoDeDebito();
                cartao.setNumero(gerarNumeroCartao());
                cartao.setSenha(passwordEncoder.encode(validarSenhaCartao(request.senha())));

                BigDecimal limiteDiario = calcularLimiteDiarioCliente(conta.getCliente());
                cartao.setDataValidade(LocalDate.now().plusYears(3));

                cartao.setLimiteDiarioTransacao(limiteDiario);
                cartao.setConta(conta);

                cartao.setCliente(conta.getCliente());
                cartao.setAtivoOuDesativo(true);


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

                cartao.setDataValidade(LocalDate.now().plusYears(3));
                cartao.setDiaVencimentoFatura(1);
                LocalDate proximoMes = LocalDate.now().plusMonths(1);
                cartao.setDataProximoVencimento(
                        LocalDate.of(proximoMes.getYear(), proximoMes.getMonth(), cartao.getDiaVencimentoFatura())
                );

                cartao.setContaCorrente(contaCorrente);
                cartao.setCliente(cliente);

                cartao.setAtivoOuDesativo(true);

                cartaoCreditoRepository.save(cartao);
                criarSeguroFraudeAutomatico(cartao);

                if (request instanceof CartaoRequestDTO creditoRequest &&
                        creditoRequest.adquirirSeguroViagem()) {
                    seguroService.contratarSeguroViagem(cartao.getId());
                }

                return new CartaoResponseDTO(cartao);
            }

        }catch ( BusinessException e) {
            throw e;

        } catch (Exception e) {
            throw new BusinessException("Erro ao emitir cartão: " + e.getMessage());
        }

    }
    public void atualizarDataVencimentoFatura(CartaoDeCredito cartao) {
        LocalDate proximoMes = LocalDate.now().plusMonths(1);
        cartao.setDataProximoVencimento(
                LocalDate.of(proximoMes.getYear(), proximoMes.getMonth(), cartao.getDiaVencimentoFatura())
        );
        cartaoCreditoRepository.save(cartao);
    }
    private void criarSeguroFraudeAutomatico(CartaoDeCredito cartao) {
        Seguro seguro = Seguro.builder()
                .tipo(TipoSeguro.FRAUDE)
                .dataContratacao(LocalDate.now())
                .valorMensal(BigDecimal.ZERO)
                .cartao(cartao)
                .build();

        seguroRepository.save(seguro);
    }

    private BigDecimal calcularLimitePorCliente(Cliente cliente){
        return switch (cliente.getTipoCliente()) {
            case COMUM -> new BigDecimal(1000);
            case SUPER -> new BigDecimal(5000);
            case PREMIUM -> new BigDecimal(10000);
            default -> throw new TipoInvalidoException("Tipo de cliente inválido");
        };
    }
    private BigDecimal calcularLimiteDiarioCliente(Cliente cliente){
        return switch (cliente.getTipoCliente()) {
            case COMUM -> new BigDecimal(1000);
            case SUPER -> new BigDecimal(2000);
            case PREMIUM -> new BigDecimal(5000);
            default -> throw new TipoInvalidoException("Tipo de cliente inválido");
        };
    }

    private String validarSenhaCartao(String senha) {
        if (senha == null || !senha.matches("\\d{4}")) {
            throw new BusinessException("Senha deve conter exatamente 4 dígitos numéricos");
        }
        return senha;
    }
    private String gerarNumeroCartao() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 16; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
    private BigDecimal calcularLimiteDisponivel(CartaoDeCredito cartao) {
        return cartao.getLimitePreAprovado().subtract(cartao.getFaturaAtual());
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
    public PagamentoCartaoResponseDTO realizarPagamentoComCartao(String cartaoId, PagamentoCartaoRequestDTO request){
        try {
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
                return new PagamentoCartaoResponseDTO(
                        "Pagamento com cartão de débito realizado",
                        request.valor(),
                        null,
                        null
                );
            } else {
                CartaoDeCredito cartao = cartaoCreditoRepository.findById(cartaoId)
                        .orElseThrow(() -> new CartaoNaoEncontradaException("Cartão não encontrado"));
                if (!passwordEncoder.matches(request.senha(), cartao.getSenha())){
                    throw new SenhaIncorretaException("Senha incorreta");
                }
                BigDecimal limiteDisponivel = calcularLimiteDisponivel(cartao);

                if (cartao.getFaturaAtual().compareTo(cartao.getLimitePreAprovado()) > 0) {
                    throw new LimiteExcedidoException("Fatura excedeu o limite do cartão");
                }

                if (limiteDisponivel.compareTo(request.valor()) < 0) {
                    throw new LimiteExcedidoException("Limite indisponível");
                }

                cartao.setFaturaAtual(cartao.getFaturaAtual().add(request.valor()));
                cartaoCreditoRepository.save(cartao);
                return new PagamentoCartaoResponseDTO(
                        "Pagamento com cartão de crédito realizado",
                        request.valor(),
                        cartao.getLimitePreAprovado(),
                        calcularLimiteDisponivel(cartao)
                );
            }
        }catch (CartaoNaoEncontradaException | SenhaIncorretaException | LimiteExcedidoException | SaldoInsuficienteException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Erro ao realizar pagamento com cartão: " + e.getMessage());
        }

    }

    @Transactional
    public void pagarFatura(String cartaoId, PagamentoFaturaRequestDTO request) {
        try {
            CartaoDeCredito cartao = cartaoCreditoRepository.findById(cartaoId)
                    .orElseThrow(() -> new CartaoNaoEncontradaException("Cartão de crédito não encontrado"));

            Conta conta = contaRepository.findById(request.contaId())
                    .orElseThrow(() -> new ContaNaoEncontradaException("Conta não encontrada"));

            BigDecimal valorTransacoes = cartao.getFaturaAtual();
            BigDecimal valorTotal = valorTransacoes;
            BigDecimal taxaUtilizacao = BigDecimal.ZERO;

            if (aplicarTaxaUtilizacao(valorTransacoes, cartao.getLimitePreAprovado())) {
                taxaUtilizacao = calcularTaxaUtilizacao(valorTransacoes);
                valorTotal = valorTotal.add(taxaUtilizacao);
            }

            BigDecimal valorSeguros = seguroRepository.findByCartaoIdAndDataCancelamentoIsNull(cartaoId).stream()
                    .filter(s -> s.getTipo() == TipoSeguro.VIAGEM)
                    .map(Seguro::getValorMensal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            valorTotal = valorTotal.add(valorSeguros);


            if (request.valor().compareTo(valorTotal) > 0) {
                throw new PagamentoFaturaException("Valor do pagamento não pode ser maior que o total da fatura");
            }

            if (conta.getSaldo().compareTo(request.valor()) < 0) {
                throw new SaldoInsuficienteException("Saldo insuficiente para pagar a fatura");
            }

            conta.setSaldo(conta.getSaldo().subtract(request.valor()));
            BigDecimal novaFatura = valorTransacoes.subtract(request.valor());
            cartao.setFaturaAtual(novaFatura);

            Fatura fatura = new Fatura();
            fatura.setCartao(cartao);
            fatura.setValorPago(request.valor());
            fatura.setDataPagamento(LocalDate.now());
            fatura.setTotalTaxas(taxaUtilizacao.add(valorSeguros));

            contaRepository.save(conta);
            cartaoCreditoRepository.save(cartao);
            faturaRepository.save(fatura);
        } catch (CartaoNaoEncontradaException | ContaNaoEncontradaException |
                 PagamentoFaturaException | SaldoInsuficienteException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Erro ao pagar fatura: " + e.getMessage());
        }

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


    @Transactional(readOnly = true)
    public FaturaResponseDTO consultarFatura(String cartaoId) {
        CartaoDeCredito cartao = cartaoCreditoRepository.findById(cartaoId)
                .orElseThrow(() -> new CartaoNaoEncontradaException("Cartão de crédito não encontrado"));

        BigDecimal valorTransacoes = cartao.getFaturaAtual();
        BigDecimal valorTotal = valorTransacoes;

        BigDecimal taxaUtilizacao = BigDecimal.ZERO;
        if (aplicarTaxaUtilizacao(valorTransacoes, cartao.getLimitePreAprovado())) {
            taxaUtilizacao = calcularTaxaUtilizacao(valorTransacoes);
            valorTotal = valorTotal.add(taxaUtilizacao);
        }

        BigDecimal valorSeguros = seguroRepository.findByCartaoIdAndDataCancelamentoIsNull(cartaoId).stream()
                .filter(s -> s.getTipo() == TipoSeguro.VIAGEM)
                .map(Seguro::getValorMensal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        valorTotal = valorTotal.add(valorSeguros);

        List<Fatura> faturasPagas = faturaRepository.findByCartaoId(cartaoId);
        List<PagamentoFaturaDTO> historico = faturasPagas.stream()
                .map(f -> new PagamentoFaturaDTO(
                        f.getValorPago(),
                        f.getDataPagamento()
                ))
                .collect(Collectors.toList());

        return new FaturaResponseDTO(
                valorTotal,
                cartao.getDataProximoVencimento(),
                cartao.getLimitePreAprovado(),
                calcularLimiteDisponivel(cartao),
                historico,
                taxaUtilizacao,
                valorSeguros
        );
    }
    @Transactional
    public void cancelarCartao(String cartaoId) {
        try {
            if (cartaoId.startsWith("CD")) {
                CartaoDeDebito cartao = cartaoDebitoRepository.findById(cartaoId)
                        .orElseThrow(() -> new CartaoNaoEncontradaException("Cartão de débito não encontrado"));

                if (!cartao.isAtivoOuDesativo()) {
                    throw new BusinessException("Cartão já está cancelado");
                }

                cartao.setAtivoOuDesativo(false);
                cartaoDebitoRepository.save(cartao);
            } else {
                CartaoDeCredito cartao = cartaoCreditoRepository.findById(cartaoId)
                        .orElseThrow(() -> new CartaoNaoEncontradaException("Cartão de crédito não encontrado"));

                if (!cartao.isAtivoOuDesativo()) {
                    throw new BusinessException("Cartão já está cancelado");
                }

                if (cartao.getFaturaAtual().compareTo(BigDecimal.ZERO) > 0) {
                    throw new BusinessException("Não é possível cancelar o cartão com fatura pendente");
                }

                List<Seguro> segurosAtivos = seguroRepository.findByCartaoIdAndDataCancelamentoIsNull(cartaoId);
                LocalDate hoje = LocalDate.now();
                for (Seguro seguro : segurosAtivos) {
                    seguro.setDataCancelamento(hoje);
                    seguroRepository.save(seguro);
                }

                cartao.setAtivoOuDesativo(false);
                cartaoCreditoRepository.save(cartao);
            }
        }catch ( BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Erro ao cancelar cartão: " + e.getMessage());
        }

    }



}
