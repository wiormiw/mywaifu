package com.silvermaiden.mywaifu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class MyWaifuApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyWaifuApplication.class, args);
	}

}
