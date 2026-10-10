package tz.tante.rent.manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RentManagerApplication
{

	public static void main(String[] args) {
		SpringApplication.run(RentManagerApplication.class, args);
	}

}
