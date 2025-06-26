package br.com.cdb.bancoDigitalCdb.service;


import br.com.cdb.bancoDigitalCdb.dto.CartaoRequestDTO;
import br.com.cdb.bancoDigitalCdb.dto.CartaoResponseDTO;
import br.com.cdb.bancoDigitalCdb.dto.PagamentoCartaoRequestDTO;
import br.com.cdb.bancoDigitalCdb.entity.*;
import br.com.cdb.bancoDigitalCdb.handler.*;
import br.com.cdb.bancoDigitalCdb.repository.CartaoCreditoRepository;
import br.com.cdb.bancoDigitalCdb.repository.CartaoDebitoRepository;
import br.com.cdb.bancoDigitalCdb.repository.ContaRepository;
import br.com.cdb.bancoDigitalCdb.repository.FaturaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            cartao.setLimitePreAprovado(request.limiteCredito());
            cartao.setDataVencimento(LocalDate.now().plusYears(3));
            cartao.setContaCorrente((ContaCorrente) conta);
            cartaoCreditoRepository.save(cartao);
            return new CartaoResponseDTO(cartao);
        }
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


}
