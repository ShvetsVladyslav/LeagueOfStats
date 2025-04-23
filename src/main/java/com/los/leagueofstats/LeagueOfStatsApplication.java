package com.los.leagueofstats;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableConfigurationProperties
@EnableCaching
public class LeagueOfStatsApplication {

	public static void main(String[] args) {
		SpringApplication.run(LeagueOfStatsApplication.class, args);
	}

}
