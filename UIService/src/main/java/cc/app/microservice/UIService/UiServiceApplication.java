package cc.app.microservice.UIService;

import cc.app.microservice.UIService.controller.DiscoveryclientController;
import cc.app.microservice.UIService.util.DiscoveryUtil;
import cc.app.microservice.UIService.util.JwtUtil;
import com.google.gson.Gson;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@EnableCircuitBreaker
public class UiServiceApplication {

	private static final String APP_NAME = "UserInterfaceService";
	private static String discAddress;
	private static String redisIP;
	private static String hostIP;

	@Bean
	public JwtUtil getJwtUtil() {
		return new JwtUtil();
	}

	@Bean
	public RestTemplate restTemplate(){
		return new RestTemplate();
	}

	@Bean
	public DiscoveryUtil discUtil() {
		return new DiscoveryUtil();
	}

	@Bean
	public Jedis getRedisClient(){
		Jedis jedis = new Jedis(redisIP, 6379);
		jedis.auth("admin@123");
		return jedis;
	}

	@Bean
	public Gson getJsonConverter(){
		return new Gson();
	}

	public static String getDiscAddress(){
		return discAddress;
	}

	public static String getHostIP() {
		return hostIP;
	}

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(UiServiceApplication.class);

		Map<String, Object> conf = new HashMap<String, Object>();

		if(args.length != 4) {
			System.out.println("Usage: java -jar filename.jar <portNo> <discovery server IP>:<portNo> <redis IP> <hostIP>");
			return;
		}

		String PORT = args[0];
		discAddress = args[1];
		redisIP = args[2];
		hostIP = args[3];

		conf.put("server.port", PORT);
		conf.put("spring.application.name", APP_NAME);

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
