package uy.com.cb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class HolaSpringApplication{

	public static void main(String[] args) {
		SpringApplication.run(HolaSpringApplication.class, args);
	}

}
