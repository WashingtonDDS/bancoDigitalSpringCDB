package br.com.cdb.bancoDigitalCdb.service;

import br.com.cdb.bancoDigitalCdb.dto.RegisterRequestDTO;
import br.com.cdb.bancoDigitalCdb.entity.Cliente;
import br.com.cdb.bancoDigitalCdb.entity.Endereco;
import br.com.cdb.bancoDigitalCdb.handler.BusinessException;
import br.com.cdb.bancoDigitalCdb.repository.ClienteRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {
    private final ClienteRepository clienteRepository;
    private final CepApiService cepApiService;
    private final PasswordEncoder passwordEncoder;
    private final CpfService cpfService;

    public ClienteService(ClienteRepository clienteRepository, CepApiService cepApiService, PasswordEncoder passwordEncoder, CpfService cpfService) {
        this.clienteRepository = clienteRepository;
        this.cepApiService = cepApiService;
        this.passwordEncoder = passwordEncoder;
        this.cpfService = cpfService;
    }

    public Cliente criarClienteComEndereco(RegisterRequestDTO request){
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
        newCliente.setPassword(passwordEncoder.encode(request.password()));
        newCliente.setEmail(request.email());
        newCliente.setName(request.name());
        newCliente.setCpf(request.cpf());
        newCliente.setDataDeNascimento(request.dataDeNascimento());
        newCliente.setEndereco(endereco);
        newCliente.setTipoCliente(request.tipoCliente());
        newCliente.setRole(request.role());
        newCliente.setTipoDeConta(request.tipoDeConta());

        return clienteRepository.save(newCliente);
    }
    public Cliente autenticar(String email, String password){
        Cliente cliente = clienteRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!passwordEncoder.matches(password, cliente.getPassword())){
            throw new BusinessException("Senha incorreta");
        }
        return cliente;
    }
    public List<Cliente>listarTodosClientes() {
        return clienteRepository.findAll();
    }
    public Cliente detalharCliente(String cpf){
        String cpfFormatado = cpf.replaceAll("\\D", "");
        if (!cpfService.validarCpf(cpfFormatado)) {
            throw new BusinessException("CPF inválido");
        }
        return clienteRepository.findByCpf(cpf).orElseThrow(() -> new BusinessException("Cliente não encontrado"));
    }
}
