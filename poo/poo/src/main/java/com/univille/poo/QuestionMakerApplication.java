package com.univille.poo;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class QuestionMakerApplication {

	public static void main(String[] args) {
		try {
			Dotenv dotenv = Dotenv.configure()
					.ignoreIfMissing()
					.load();

			dotenv.entries().forEach(entry -> {
				System.setProperty(entry.getKey(), entry.getValue());
			});

			System.out.println("Arquivo .env carregado com sucesso!");
			System.out.println("GEMINI_API_KEY: " + (dotenv.get("GEMINI_API_KEY") != null ? "configurado" : "NÃO ENCONTRADO"));
		} catch (Exception e) {
			System.out.println("⚠Arquivo .env não encontrado. Usando variáveis de ambiente do sistema.");
			System.out.println("Erro: " + e.getMessage());
		}

		SpringApplication.run(QuestionMakerApplication.class, args);
	}

}
