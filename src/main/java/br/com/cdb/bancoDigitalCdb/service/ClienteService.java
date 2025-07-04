package br.com.cdb.bancoDigitalCdb.service;

import br.com.cdb.bancoDigitalCdb.dto.AtualizacaoParcialClienteDTO;
import br.com.cdb.bancoDigitalCdb.dto.RegisterRequestDTO;
import br.com.cdb.bancoDigitalCdb.entity.Cliente;
import br.com.cdb.bancoDigitalCdb.entity.Conta;
import br.com.cdb.bancoDigitalCdb.entity.Endereco;
import br.com.cdb.bancoDigitalCdb.handler.BusinessException;
import br.com.cdb.bancoDigitalCdb.repository.ClienteRepository;
import br.com.cdb.bancoDigitalCdb.repository.ContaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClienteService {
    private final ClienteRepository clienteRepository;
    private final CepApiService cepApiService;
    private final PasswordEncoder passwordEncoder;
    private final CpfService cpfService;
    private final ContaRepository contaRepository;

    public ClienteService(ClienteRepository clienteRepository, CepApiService cepApiService, PasswordEncoder passwordEncoder, CpfService cpfService, ContaRepository contaRepository) {
        this.clienteRepository = clienteRepository;
        this.cepApiService = cepApiService;
        this.passwordEncoder = passwordEncoder;
        this.cpfService = cpfService;
        this.contaRepository = contaRepository;
    }

    public Cliente criarClienteComEndereco(RegisterRequestDTO request){
        try {
            if (request == null) {
                throw new IllegalArgumentException("Request não pode ser nulo");
            }

            Endereco endereco;
            if (request.endereco().getCep() != null && !request.endereco().getCep().isBlank()){
                endereco = cepApiService.buscarEnderecoPorCep(request.endereco().getCep());
                endereco.setNumeroDaCasa(request.endereco().getNumeroDaCasa());
                endereco.setComplemento(request.endereco().getComplemento());
            }else {
                endereco = new Endereco();
                endereco.setRua(request.endereco().getRua());
                endereco.setNumeroDaCasa(request.endereco().getNumeroDaCasa());
                endereco.setComplemento(request.endereco().getComplemento());
                endereco.setBairro(request.endereco().getBairro());
                endereco.setCidade(request.endereco().getCidade());
                endereco.setEstado(request.endereco().getEstado());
                endereco.setCep(request.endereco().getCep());
            }
            Cliente newCliente = new Cliente();
            newCliente.setPassword(passwordEncoder.encode(validarSenha(request.password())));
            newCliente.setEmail(request.email());
            newCliente.setNome(request.nome());
            newCliente.setCpf(request.cpf());
            newCliente.setDataDeNascimento(request.dataDeNascimento());
            newCliente.setEndereco(endereco);
            newCliente.setTipoCliente(request.tipoCliente());
            newCliente.setRole(request.role());

            return clienteRepository.save(newCliente);
        }catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Erro ao criar cliente: " + e.getMessage());
        }

    }
    public Cliente autenticar(String email, String password){
        try {
            Cliente cliente = clienteRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

            if (!passwordEncoder.matches(password, cliente.getPassword())){
                throw new BusinessException("Senha incorreta");
            }
            return cliente;
        }catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Erro ao autenticar cliente: " + e.getMessage());
        }

    }
    private String validarSenha(String senha) {
        try {
            if (senha == null || senha.length() < 8) {
                throw new BusinessException("Senha deve ter no mínimo 8 caracteres");
            }

            if (!senha.matches(".*[A-Z].*")) {
                throw new BusinessException("Senha deve conter pelo menos 1 letra maiúscula");
            }

            if (!senha.matches(".*[a-z].*")) {
                throw new BusinessException("Senha deve conter pelo menos 1 letra minúscula");
            }

            if (!senha.matches(".*\\d.*")) {
                throw new BusinessException("Senha deve conter pelo menos 1 número");
            }
            return senha;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Erro ao validar senha: " + e.getMessage());
        }

    }
    public List<Cliente>listarTodosClientes() {
        try {
            return clienteRepository.findAll();
        }catch (Exception e) {
            throw new BusinessException("Erro ao listar clientes: " + e.getMessage());
        }
    }
    public Cliente detalharCliente(String cpf){
        try {
            String cpfFormatado = cpf.replaceAll("\\D", "");
            if (!cpfService.validarCpf(cpfFormatado)) {
                throw new BusinessException("CPF inválido");
            }
            return clienteRepository.findByCpf(cpf).orElseThrow(() -> new BusinessException("Cliente não encontrado"));
        }catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Erro ao detalhar cliente: " + e.getMessage());
        }

    }

    public  Cliente atualizarCliente(String cpf, AtualizacaoParcialClienteDTO request){
        try {
            String cpfFormatado = cpf.replaceAll("\\D", "");
            if (!cpfService.validarCpf(cpfFormatado)) {
                throw new BusinessException("CPF inválido");
            }

            Cliente cliente = clienteRepository.findByCpf(cpf).orElseThrow(() -> new BusinessException("Cliente não encontrado"));

            if (request.nome() != null) {
                cliente.setNome(request.nome());
            }
            if (request.email() != null) {
                if (!cliente.getEmail().equals(request.email())) {
                    if (clienteRepository.existsByEmail(request.email())) {
                        throw new BusinessException("Email já cadastrado");
                    }
                    cliente.setEmail(request.email());
                }
            }
            if (request.dataDeNascimento() != null) {
                cliente.setDataDeNascimento(request.dataDeNascimento());
            }
            if (request.endereco() != null) {
                cliente.setEndereco(request.endereco());
            }
            if (request.tipoCliente() != null) {
                cliente.setTipoCliente(request.tipoCliente());
            }

            return clienteRepository.save(cliente);
        }catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Erro ao atualizar cliente: " + e.getMessage());
        }

    }
    public Cliente buscarClientePorId(String id) {
        try {
            return clienteRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado com ID: " + id));
        }catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Erro ao buscar cliente por ID: " + e.getMessage());
        }
     }

    public void deleteCliente(String id) {
        try {
            Cliente cliente = clienteRepository.findById(id)
                    .orElseThrow(() -> new BusinessException("Cliente não encontrado"));

            List<Conta> contas = contaRepository.findByClienteId(id);
            List<Conta> contasComSaldo = contas.stream()
                    .filter(conta -> conta.getSaldo().compareTo(BigDecimal.ZERO) > 0)
                    .toList();

            if (!contasComSaldo.isEmpty()) {
                String mensagem = contasComSaldo.stream()
                        .map(conta -> "Conta " + conta.getNumeroDaConta() + ": R$ " + conta.getSaldo())
                        .collect(Collectors.joining("\n", "Contas com saldo:\n", ""));
                throw new BusinessException(mensagem);
            }

            contaRepository.deleteAll(contas);
            clienteRepository.delete(cliente);
        }catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Erro ao deletar cliente: " + e.getMessage());
        }

    }
}
