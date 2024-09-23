package com.aluradevsafios.LiterAlura;

import com.aluradevsafios.LiterAlura.principal.Principal;
import com.aluradevsafios.LiterAlura.repository.LibrosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LiterAluraApplication implements CommandLineRunner {

	@Autowired
	private LibrosRepository repository;

	public static void main(String[] args) {
		SpringApplication.run(LiterAluraApplication.class, args);
	}

	@Override
	public void run(String[] args) {
		Principal principal = new Principal(repository);

		principal.mostrarMenu();
	}

}
