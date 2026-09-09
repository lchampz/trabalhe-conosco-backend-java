package br.com.docket.cartorios;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CartoriosApplication {

	public static void main(String[] args) {
		SpringApplication.run(CartoriosApplication.class, args);
	}

}
