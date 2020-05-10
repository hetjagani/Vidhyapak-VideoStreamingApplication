package com.discovery.discoveryserver;

import com.discovery.discoveryserver.model.DiscoveryInfo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SpringBootApplication
public class DiscoveryserverApplication {

	private static final String APP_NAME = "DiscoveryServer";
	private static String databaseIP;

	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

	private static ConcurrentHashMap<DiscoveryInfo, Boolean> isReceived = new ConcurrentHashMap<DiscoveryInfo, Boolean>();

	public static ConcurrentHashMap<DiscoveryInfo, Boolean> getServicesMap() {
		return isReceived;
	}

	public static void setServicesMap(ConcurrentHashMap<DiscoveryInfo, Boolean> servicesData) {
		DiscoveryserverApplication.isReceived = servicesData;
	}

	public static void main(String[] args) {

		SpringApplication app = new SpringApplication(DiscoveryserverApplication.class);
		Map<String, Object> conf = new HashMap<String, Object>();

		if(args.length != 2) {
			System.out.println("Usage: java -jar filename.jar <portNo> <database IP>");
			return;
		}
		String PORT = args[0];
		databaseIP = args[1];

		conf.put("server.port", PORT);
		conf.put("spring.application.name", APP_NAME);
		conf.put("spring.datasource.url", "jdbc:mysql://"+ databaseIP +":3306/DiscoveryService");

		app.setDefaultProperties(conf);

		app.run(args);

		DiscoveryserverController controller = new DiscoveryserverController();
		while(true) {
			controller.checkServicesStatus();
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
