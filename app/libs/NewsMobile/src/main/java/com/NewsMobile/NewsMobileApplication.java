package com.NewsMobile;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan
public class NewsMobileApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(NewsMobileApplication.class, args);
	}
	

}
