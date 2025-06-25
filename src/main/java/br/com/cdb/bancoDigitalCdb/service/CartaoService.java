package br.com.cdb.bancoDigitalCdb.service;


import br.com.cdb.bancoDigitalCdb.dto.CartaoRequestDTO;
import br.com.cdb.bancoDigitalCdb.dto.CartaoResponseDTO;
import br.com.cdb.bancoDigitalCdb.repository.CartaoCreditoRepository;
import br.com.cdb.bancoDigitalCdb.repository.CartaoDebitoRepository;
import br.com.cdb.bancoDigitalCdb.repository.ContaRepository;
import br.com.cdb.bancoDigitalCdb.repository.FaturaRepository;
import org.springframework.stereotype.Service;

@Service
public class CartaoService {
    private final CartaoDebitoRepository cartaoDebitoRepository;
    private final CartaoCreditoRepository cartaoCreditoRepository;
    private final ContaRepository contaRepository;
    private final FaturaRepository faturaRepository;

    public CartaoService(CartaoDebitoRepository cartaoDebitoRepository, CartaoCreditoRepository cartaoCreditoRepository, ContaRepository contaRepository, FaturaRepository faturaRepository) {
        this.cartaoDebitoRepository = cartaoDebitoRepository;
        this.cartaoCreditoRepository = cartaoCreditoRepository;
        this.contaRepository = contaRepository;
        this.faturaRepository = faturaRepository;
    }

    public CartaoResponseDTO emitirCartao(CartaoRequestDTO request){

    }
}
