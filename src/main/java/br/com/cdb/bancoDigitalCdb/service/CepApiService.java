package br.com.cdb.bancoDigitalCdb.service;

import br.com.cdb.bancoDigitalCdb.entity.Endereco;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CepApiService {
    private RestTemplate restTemplate;

    @Autowired
    public CepApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Endereco buscarEnderecoPorCep(String cep){
        String url = "https://brasilapi.com.br/api/cep/v1/"+cep;

        BrasilApiResponse response = restTemplate.getForObject(url, BrasilApiResponse.class);

        if (response == null){
            throw new RuntimeException("CEP não encontrado");
        }
        return response.toEndereco();
    }
    @Getter
    @Setter
    private static class BrasilApiResponse{
        private String cep;
        private String state;
        private String city;
        private String neighborhood;
        private String street;


        public Endereco toEndereco() {
            Endereco endereco = new Endereco();
            endereco.setRua(this.street);
            endereco.setBairro(this.neighborhood);
            endereco.setCidade(this.city);
            endereco.setEstado(this.state);
            endereco.setCep(this.cep);
            return endereco;
        }
    }



}
