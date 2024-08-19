package com.joe.benefits;

import org.springframework.boot.SpringApplication;

public class TestBenefitsApplication {

	public static void main(String[] args) {
		SpringApplication.from(BenefitsApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
