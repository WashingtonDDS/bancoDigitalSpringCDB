package br.com.cdb.bancoDigitalCdb.service;

import br.com.cdb.bancoDigitalCdb.entity.Cliente;
import br.com.cdb.bancoDigitalCdb.entity.Conta;
import br.com.cdb.bancoDigitalCdb.handler.BusinessException;
import br.com.cdb.bancoDigitalCdb.repository.ClienteRepository;
import br.com.cdb.bancoDigitalCdb.repository.ContaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PixService {
    private final ContaRepository contaRepository;
    private final ClienteRepository clienteRepository;

    public PixService(ContaRepository contaRepository, ClienteRepository clienteRepository) {
        this.contaRepository = contaRepository;
        this.clienteRepository = clienteRepository;
    }
    @Transactional
    public void processarPix(Conta contaOrigem, String cpfDestino, BigDecimal valor) {
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Valor do Pix deve ser positivo");
        }

        if (contaOrigem.getSaldo().compareTo(valor) < 0) {
            throw new BusinessException("Saldo insuficiente");
        }

        Cliente clienteDestino = clienteRepository.findByCpf(cpfDestino)
                .orElseThrow(() -> new BusinessException("Cliente destino não encontrado"));

        List<Conta> contasDestino = contaRepository.findByClienteId(clienteDestino.getId());

        if (contasDestino.isEmpty()) {
            throw new BusinessException("Conta destino não encontrada para o cliente");
        }
        Conta contaDestino = contasDestino.get(0);

        contaOrigem.setSaldo(contaOrigem.getSaldo().subtract(valor));
        contaDestino.setSaldo(contaDestino.getSaldo().add(valor));

        contaRepository.save(contaOrigem);
        contaRepository.save(contaDestino);

    }
}
