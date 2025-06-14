package br.com.cdb.bancoDigitalCdb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "br.com.cdb.bancoDigitalCdb.repository")
@EntityScan(basePackages = "br.com.cdb.bancoDigitalCdb.entity")
public class BancoDigitalCodigoDeBaseApplication {

	public static void main(String[] args) {
		SpringApplication.run(BancoDigitalCodigoDeBaseApplication.class, args);
	}

}
