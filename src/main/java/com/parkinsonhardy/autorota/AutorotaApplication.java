package com.parkinsonhardy.autorota;

import org.joda.time.DateTimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AutorotaApplication {

	public static void main(String[] args) {
		DateTimeZone.setDefault(DateTimeZone.UTC);
		SpringApplication.run(AutorotaApplication.class, args);
	}
}
