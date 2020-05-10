package com.gyapak.userinformation;

import com.gyapak.userinformation.controller.DiscoveryclientController;
import com.gyapak.userinformation.filter.JwtRequestFilter;
import com.gyapak.userinformation.util.JwtUtil;
import com.gyapak.userinformation.util.PersonRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@EnableCaching
public class UserInformationApplication {

	private static final String APP_NAME = "UserInformation";
	private static String discAddress;
	private static String databaseIP;
	private static String redisIP;

	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

	@Bean
	public JwtRequestFilter getJwtFilter() {
		return new JwtRequestFilter();
	}

	@Bean
	public JwtUtil getJwtUtil() {
		return new JwtUtil();
	}

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(UserInformationApplication.class);

		Map<String, Object> conf = new HashMap<String, Object>();

		if(args.length != 4) {
			System.out.println("Usage: java -jar filename.jar <portNo> <discovery server IP>:<portNo> <database IP> <redis IP>");
			return;
		}

		String PORT = args[0];
		discAddress = args[1];
		databaseIP = args[2];
		redisIP = args[3];

		conf.put("server.port", PORT);
		conf.put("spring.application.name", APP_NAME);
		conf.put("spring.datasource.jdbcUrl","jdbc:mysql://" + databaseIP + ":3306/UserInfo");
		conf.put("spring.second-db.jdbcUrl","jdbc:mysql://" + databaseIP + ":3306/AuthenticationService");
		conf.put("spring.redis.host",redisIP);

		app.setDefaultProperties(conf);
		app.run(args);

		DiscoveryclientController controller = new DiscoveryclientController(APP_NAME, Integer.parseInt(PORT), discAddress);
		Thread heartbeatThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true)
					controller.sendHeartBeat();
			}
		});
		heartbeatThread.start();
	}

}
