package com.br.isabelaModas;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class IsabelaModasApplication {

	public static void main(String[] args) {

		String activeProfile = System.getProperty("spring.profiles.active");
		if (activeProfile == null || !activeProfile.equals("test")) {
			Dotenv dotenv = Dotenv.load();
			System.setProperty("DB_URL", dotenv.get("DB_URL"));
			System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
			System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
			System.setProperty("MERCADOPAGO_ACCESS_TOKEN", dotenv.get("MERCADOPAGO_ACCESS_TOKEN", ""));
			System.setProperty("MERCADOPAGO_WEBHOOK_URL", dotenv.get("MERCADOPAGO_WEBHOOK_URL", "https://sua-url-producao.com/api/webhook/notificacao"));
			System.setProperty("WHATSAPP_TOKEN", dotenv.get("WHATSAPP_TOKEN", ""));
			System.setProperty("WHATSAPP_PHONE_NUMBER_ID", dotenv.get("WHATSAPP_PHONE_NUMBER_ID", ""));
			System.setProperty("JWT_SECRET_KEY", dotenv.get("JWT_SECRET_KEY", "minhaChaveSuperSecretaDeSeguranca1234567890"));
		}
		SpringApplication.run(IsabelaModasApplication.class, args);
	}


}





