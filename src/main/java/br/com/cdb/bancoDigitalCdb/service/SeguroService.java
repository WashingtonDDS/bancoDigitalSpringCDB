package br.com.cdb.bancoDigitalCdb.service;

import br.com.cdb.bancoDigitalCdb.dto.ContratarSeguroRequestDTO;
import br.com.cdb.bancoDigitalCdb.dto.SeguroDetalhesDTO;
import br.com.cdb.bancoDigitalCdb.dto.SeguroResponseDTO;
import br.com.cdb.bancoDigitalCdb.entity.CartaoDeCredito;
import br.com.cdb.bancoDigitalCdb.entity.Seguro;
import br.com.cdb.bancoDigitalCdb.entity.TipoCliente;
import br.com.cdb.bancoDigitalCdb.entity.TipoSeguro;
import br.com.cdb.bancoDigitalCdb.handler.*;
import br.com.cdb.bancoDigitalCdb.repository.CartaoCreditoRepository;
import br.com.cdb.bancoDigitalCdb.repository.SeguroRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class SeguroService {

    private final SeguroRepository seguroRepository;
    private final CartaoCreditoRepository cartaoCreditoRepository;

    public SeguroService(SeguroRepository seguroRepository, CartaoCreditoRepository cartaoCreditoRepository) {
        this.seguroRepository = seguroRepository;
        this.cartaoCreditoRepository = cartaoCreditoRepository;
    }

    @Transactional
    public SeguroResponseDTO contratarSeguro(ContratarSeguroRequestDTO request){
        CartaoDeCredito cartao = cartaoCreditoRepository.findById(request.cartaoId())
                .orElseThrow(() -> new CartaoNaoEncontradaException("Cartão não encontrado"));
        if (seguroRepository.existsAtivoByCartaoIdAndTipo(request.cartaoId(),request.tipo())){
            throw new SeguroAtivoException("Este seguro já está ativo para o cartão");
        }

        BigDecimal valorMensal = calcularValorMensal(request.tipo(),cartao.getContaCorrente().getCliente().getTipoCliente());
        Seguro seguro = Seguro.builder()
                .tipo(request.tipo())
                .dataContratacao(LocalDate.now())
                .valorMensal(valorMensal)
                .cartao(cartao)
                .build();

        seguro = seguroRepository.save(seguro);
        return mapToDto(seguro);
    }

    private BigDecimal calcularValorMensal(TipoSeguro tipo, TipoCliente tipoCliente) {
        return switch (tipo) {
            case FRAUDE -> BigDecimal.ZERO;
            case VIAGEM -> tipoCliente == TipoCliente.PREMIUM ?
                    BigDecimal.ZERO :
                    new BigDecimal("50.00");
        };
    }

    public SeguroDetalhesDTO detalharSeguro(String id){
        Seguro seguro = seguroRepository.findById(id)
                .orElseThrow(() -> new SeguroNaoEncontradoException("Seguro não encontrado"));

        BigDecimal valorCobertura = (seguro.getTipo() == TipoSeguro.FRAUDE) ?
                new BigDecimal("100000.00") : null;

        return new SeguroDetalhesDTO(
                seguro.getId(),
                seguro.getNumeroApolice(),
                seguro.getTipo(),
                seguro.getDataContratacao(),
                seguro.getDataCancelamento(),
                seguro.getValorMensal(),
                seguro.getCartao().getId(),
                valorCobertura,
                seguro.getCartao().getContaCorrente().getCliente().getTipoCliente().name()
        );

    }

    public List<SeguroResponseDTO>listarSegurosPorCartao(String cartaoId){
        return seguroRepository.findByCartaoId(cartaoId).stream()
                .map(this::mapToDto)
                .toList();
    }

    @Transactional
    public void cancelarSeguro(String id) {
        Seguro seguro = seguroRepository.findById(id)
                .orElseThrow(() -> new SeguroNaoEncontradoException("Seguro não encontrado"));

        if (seguro.getTipo() == TipoSeguro.FRAUDE) {
            throw new OperacaoNaoPermitidaException("Seguro de fraude não pode ser cancelado");
        }

        if (seguro.getDataCancelamento() != null) {
            throw new SeguroJaCanceladoException("Seguro já cancelado anteriormente");
        }

        seguro.setDataCancelamento(LocalDate.now());
        seguroRepository.save(seguro);
    }

    private SeguroResponseDTO mapToDto(Seguro seguro) {
        return new SeguroResponseDTO(
                seguro.getId(),
                seguro.getNumeroApolice(),
                seguro.getTipo(),
                seguro.getDataContratacao(),
                seguro.getDataCancelamento(),
                seguro.getValorMensal(),
                seguro.getCartao().getId()
        );
    }

}
