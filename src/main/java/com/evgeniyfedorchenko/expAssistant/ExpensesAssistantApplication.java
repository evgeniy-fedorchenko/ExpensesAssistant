package com.evgeniyfedorchenko.expAssistant;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition
@SpringBootApplication
public class ExpensesAssistantApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExpensesAssistantApplication.class, args);
	}

}
