package com.aluraone.literalura;

import com.aluraone.literalura.principal.Principal;

import com.aluraone.literalura.repository.AutorRepository;
import com.aluraone.literalura.repository.LibroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

//implementa comandLineRunner para usar el metodo run
//excluimos data source para evitar que use la base de datos que aun no hems configurado
@SpringBootApplication//(exclude = {DataSourceAutoConfiguration.class })
public class LiteraluraApplication implements CommandLineRunner {

	@Autowired
	private LibroRepository libroRepository;

	@Autowired
	private AutorRepository autorRepository;

	public static void main(String[] args) {
		SpringApplication.run(LiteraluraApplication.class, args);
	}

	@Override
	public void run(String... args){
		Principal principal = new Principal(libroRepository, autorRepository);
		principal.consultaEjemplo();
	}
}
