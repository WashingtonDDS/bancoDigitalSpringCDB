package br.com.cdb.bancoDigitalCdb.security;

import br.com.cdb.bancoDigitalCdb.entity.Cliente;
import br.com.cdb.bancoDigitalCdb.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class CustomClientDetailsService implements UserDetailsService {

    @Autowired
    private ClienteRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Cliente cliente = this.repository.findByEmail(username).orElseThrow(()-> new  UsernameNotFoundException("Cliente não encontrado com o email: " + username));
        return new org.springframework.security.core.userdetails.User(cliente.getEmail(), cliente.getPassword(), new ArrayList<>());
    }
}
