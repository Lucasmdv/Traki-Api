package com.traki.trakiapi;

import com.traki.trakiapi.config.AppEnvConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TrakiApiApplication {

	public static void main(String[] args) {
		AppEnvConfig.loadEnv();
		SpringApplication.run(TrakiApiApplication.class, args);
	}

}
