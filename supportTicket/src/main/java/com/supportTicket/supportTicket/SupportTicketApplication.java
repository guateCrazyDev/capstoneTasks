package com.supportTicket.supportTicket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class SupportTicketApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.configure().directory("supportTicket\\.env")
				.load();

		System.setProperty("PORT", dotenv.get("PORT"));
		System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
		System.setProperty("JWT_SECRET", dotenv.get("JWT_SECRET"));
		System.setProperty("file.upload-dir", dotenv.get("FILE_UPLOAD_DIR"));
		SpringApplication.run(SupportTicketApplication.class, args);

	}

}
