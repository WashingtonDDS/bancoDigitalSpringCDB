package br.com.cdb.bancoDigitalCdb.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomClientDetailsService clientDetailsService;

    @Autowired
    SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST, "/clientes").permitAll()
                        .requestMatchers(HttpMethod.GET, "/clientes/{id}").hasRole("USER")
                        .requestMatchers(HttpMethod.PUT, "/clientes/{id}").hasRole("USER")
                        .requestMatchers(HttpMethod.DELETE,"/clientes/{id}").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/clientes").hasRole("USER")

                        .requestMatchers(HttpMethod.POST, "/contas").hasRole("ADMIM")
                        .requestMatchers(HttpMethod.GET, "/contas/{id}").hasRole("ADMIM")
                        .requestMatchers(HttpMethod.POST, "/contas/{id}/transferencia").hasRole("ADMIM")
                        .requestMatchers(HttpMethod.GET, "/contas/{id}/saldo").hasRole("ADMIM")
                        .requestMatchers(HttpMethod.POST, "/contas/{id}/pix").hasRole("ADMIM")
                        .requestMatchers(HttpMethod.POST, "/contas/{id}/deposito").hasRole("ADMIM")
                        .requestMatchers(HttpMethod.POST, "/contas/{id}/saque").hasRole("ADMIM")
                        .requestMatchers(HttpMethod.PUT, "/contas/{id}/manutencao").hasRole("ADMIM")
                        .requestMatchers(HttpMethod.PUT, "/contas/{id}/rendimentos").hasRole("ADMIM")

                        .requestMatchers(HttpMethod.POST, "/cartoes").hasRole("ADMIM")
                        .requestMatchers(HttpMethod.GET, "/cartoes/{id}").hasRole("ADMIM")
                        .requestMatchers(HttpMethod.POST, "//cartoes/{id}/pagamento").hasRole("ADMIM")
                        .requestMatchers(HttpMethod.PUT, "/cartoes/{id}/limite").hasRole("ADMIM")
                        .requestMatchers(HttpMethod.PUT, "/cartoes/{id}/status").hasRole("ADMIM")
                        .requestMatchers(HttpMethod.PUT, "/cartoes/{id}/senha").hasRole("ADMIM")
                        .requestMatchers(HttpMethod.GET, "/cartoes/{id}/fatura").hasRole("ADMIM")
                        .requestMatchers(HttpMethod.POST, "/cartoes/{id}/fatura/pagamento").hasRole("ADMIM")
                        .requestMatchers(HttpMethod.PUT, "/cartoes/{id}/limite-diario").hasRole("ADMIM")

                        .requestMatchers(HttpMethod.POST, "/seguros").hasRole("ADMIM")
                        .requestMatchers(HttpMethod.GET, "/seguros/{id}").hasRole("ADMIM")
                        .requestMatchers(HttpMethod.GET, "/seguros").hasRole("ADMIM")
                        .requestMatchers(HttpMethod.PUT, "/seguros/{id}/cancelar").hasRole("ADMIM")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
