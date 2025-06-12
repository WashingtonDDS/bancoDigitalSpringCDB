package br.com.cdb.bancoDigitalCdb.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CepApiService {
    private RestTemplate restTemplate;

    public String getCep(){
        String url = "https://brasilapi.com.br/api/cep/v1/{cep}";
        ResponseEntity<String> forEntity = restTemplate.getForEntity(url, String.class);

        return forEntity.getBody();
    }
}
