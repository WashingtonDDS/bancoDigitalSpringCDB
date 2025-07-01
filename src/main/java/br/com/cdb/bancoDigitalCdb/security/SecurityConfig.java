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
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()


                        .requestMatchers(HttpMethod.POST, "/conta/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/conta/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/esqueciMinhasenha/**").permitAll()


                        .requestMatchers(HttpMethod.GET, "/clientes/{id}").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/clientes/{id}").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE,"/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/cliente/clientes").hasRole("ADMIN")


                        .requestMatchers(HttpMethod.POST, "/contas").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/contas/{id}").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/{id}/transferencia").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/{id}/saldo").hasRole("USER")
                        .requestMatchers(HttpMethod.POST, "/{id}/pix").hasRole("USER")
                        .requestMatchers(HttpMethod.POST, "/{id}/deposito").hasRole("USER")
                        .requestMatchers(HttpMethod.POST, "/{id}/saque").hasRole("USER")
                        .requestMatchers(HttpMethod.PUT, "/{id}/manutencao").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/{id}/rendimentos").hasRole("ADMIN")


                        .requestMatchers(HttpMethod.POST, "/cartoes").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/cartoes/{id}").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/cartoes/{id}/pagamento").hasRole("USER")
                        .requestMatchers(HttpMethod.PUT, "/cartoes/{id}/limite").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/cartoes/{id}/status").hasRole("USER")
                        .requestMatchers(HttpMethod.PUT, "/cartoes/{id}/senha").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/cartoes/{id}/fatura").hasRole("USER")
                        .requestMatchers(HttpMethod.POST, "/cartoes/{id}/fatura/pagamento").hasRole("USER")
                        .requestMatchers(HttpMethod.PUT, "/cartoes/{id}/limite-diario").hasRole("USER")


                        .requestMatchers(HttpMethod.POST, "/seguros").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/seguros").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/seguros/{id}").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/seguros/apolice/{numeroApolice}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/seguros/cartao/{cartaoId}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/seguros/{id}/cancelar").hasRole("USER")

                        .anyRequest().authenticated()
                )
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.disable()))
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