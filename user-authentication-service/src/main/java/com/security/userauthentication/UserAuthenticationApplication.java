package com.security.userauthentication;

import com.security.userauthentication.controller.DiscoveryclientController;
import com.security.userauthentication.filter.JwtRequestFilter;
import com.security.userauthentication.util.JwtUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class UserAuthenticationApplication {

	private static final String APP_NAME = "AuthenticationService";
	private static String discAddress;
	private static String databaseIP;

	@Bean
	public JwtUtil getJwtUtil() {
		return new JwtUtil();
	}

	@Bean
	public JwtRequestFilter getJwtFilter() {
		return new JwtRequestFilter();
	}

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(UserAuthenticationApplication.class);

		Map<String, Object> conf = new HashMap<String, Object>();

		if(args.length != 3) {
			System.out.println("Usage: java -jar filename.jar <portNo> <discovery server IP>:<portNo> <database IP>");
			return;
		}
		String PORT = args[0];
		discAddress = args[1];
		databaseIP = args[2];

		conf.put("server.port", PORT);
		conf.put("spring.application.name", APP_NAME);
		conf.put("spring.datasource.url", "jdbc:mysql://"+ databaseIP +":3306/AuthenticationService");

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
