package com.los.leagueofstats;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class LeagueOfStatsApplication {

	public static void main(String[] args) {
		SpringApplication.run(LeagueOfStatsApplication.class, args);
	}

}
