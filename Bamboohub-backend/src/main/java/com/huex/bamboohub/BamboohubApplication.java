package com.huex.bamboohub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class BamboohubApplication {

	public static void main(String[] args) {
		SpringApplication.run(BamboohubApplication.class, args);
	}

}
