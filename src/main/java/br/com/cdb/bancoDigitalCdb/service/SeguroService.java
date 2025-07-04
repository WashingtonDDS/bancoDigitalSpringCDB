package br.com.cdb.bancoDigitalCdb.service;

import br.com.cdb.bancoDigitalCdb.dto.ContratarSeguroDTO;
import br.com.cdb.bancoDigitalCdb.dto.SeguroDetalhesDTO;
import br.com.cdb.bancoDigitalCdb.dto.SeguroDisponivelDTO;
import br.com.cdb.bancoDigitalCdb.dto.SeguroResponseDTO;
import br.com.cdb.bancoDigitalCdb.entity.*;
import br.com.cdb.bancoDigitalCdb.handler.*;
import br.com.cdb.bancoDigitalCdb.repository.CartaoCreditoRepository;
import br.com.cdb.bancoDigitalCdb.repository.SeguroRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class SeguroService {

    private static final BigDecimal TAXA_VIAGEM = new BigDecimal("50.00");

    private final SeguroRepository seguroRepository;
    private final CartaoCreditoRepository cartaoCreditoRepository;


    public SeguroService(SeguroRepository seguroRepository, CartaoCreditoRepository cartaoCreditoRepository) {
        this.seguroRepository = seguroRepository;
        this.cartaoCreditoRepository = cartaoCreditoRepository;
    }

    @Transactional
    public SeguroResponseDTO contratarSeguroViagem(String cartaoId) {
        try {
            CartaoDeCredito cartao = cartaoCreditoRepository.findCartaoComCliente(cartaoId)
                    .orElseThrow(() -> new CartaoNaoEncontradaException("Cartão não encontrado"));

            if (seguroRepository.existsAtivoByCartaoIdAndTipo(cartaoId, TipoSeguro.VIAGEM)) {
                throw new SeguroAtivoException("Seguro viagem já está ativo para este cartão");
            }

            Seguro seguro = Seguro.builder()
                    .tipo(TipoSeguro.VIAGEM)
                    .dataContratacao(LocalDate.now())
                    .valorMensal(calcularTaxaViagem(cartao.getContaCorrente().getCliente()))
                    .cartao(cartao)
                    .build();

            return mapToDto(seguroRepository.save(seguro));
        }catch (CartaoNaoEncontradaException | SeguroAtivoException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Erro ao contratar seguro viagem: " + e.getMessage());
        }

    }


    private BigDecimal calcularTaxaViagem(Cliente cliente) {
        return cliente.getTipoCliente() == TipoCliente.PREMIUM
                ? BigDecimal.ZERO
                : TAXA_VIAGEM;
    }

    public List<SeguroDisponivelDTO> listarSegurosDisponiveis() {
        try {
            List<SeguroDisponivelDTO> seguros = new ArrayList<>();

            seguros.add(new SeguroDisponivelDTO(
                    "VIAGEM",
                    "Seguro para Viagens",
                    "Cobre despesas médicas, extravio de bagagem e cancelamentos",
                    new BigDecimal("50.00")
            ));

            seguros.add(new SeguroDisponivelDTO(
                    "FRAUDE",
                    "Proteção contra Fraudes",
                    "Cobre transações não autorizadas até R$ 5.000,00",
                    BigDecimal.ZERO
            ));

            return seguros;
        }catch (Exception e) {
            throw new BusinessException("Erro ao listar seguros disponíveis: " + e.getMessage());
        }

    }

    public SeguroDetalhesDTO detalharSeguro(String id){
        try {
            Seguro seguro = seguroRepository.findById(id)
                    .orElseThrow(() -> new SeguroNaoEncontradoException("Seguro não encontrado"));

            BigDecimal valorCobertura = (seguro.getTipo() == TipoSeguro.FRAUDE) ?
                    new BigDecimal("5000.00") : null;

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
        }catch (SeguroNaoEncontradoException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Erro ao detalhar seguro: " + e.getMessage());
        }
    }

    public List<SeguroResponseDTO>listarSegurosPorCartao(String cartaoId){
        try {
            return seguroRepository.findByCartaoId(cartaoId).stream()
                    .map(this::mapToDto)
                    .toList();
        } catch (Exception e) {
            throw new BusinessException("Erro ao listar seguros do cartão: " + e.getMessage());
        }
    }

    @Transactional
    public void cancelarSeguro(String id) {
        try {
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
        }catch (SeguroNaoEncontradoException | OperacaoNaoPermitidaException | SeguroJaCanceladoException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Erro ao cancelar seguro: " + e.getMessage());
        }

    }

    private SeguroResponseDTO mapToDto(Seguro seguro) {
        try {
            return new SeguroResponseDTO(
                    seguro.getId(),
                    seguro.getNumeroApolice(),
                    seguro.getTipo(),
                    seguro.getDataContratacao(),
                    seguro.getDataCancelamento(),
                    seguro.getValorMensal(),
                    seguro.getCartao().getId()
            );
        } catch (Exception e) {
            throw new BusinessException("Erro ao converter seguro para DTO: " + e.getMessage());
        }
    }

}
