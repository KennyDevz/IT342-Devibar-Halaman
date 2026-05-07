package edu.cit.devibar.halaman;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class HalamanApplication {

	public static void main(String[] args) {
		SpringApplication.run(HalamanApplication.class, args);
	}

}
