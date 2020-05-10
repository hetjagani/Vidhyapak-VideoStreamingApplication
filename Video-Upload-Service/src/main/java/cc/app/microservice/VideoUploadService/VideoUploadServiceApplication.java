package cc.app.microservice.VideoUploadService;

import cc.app.microservice.VideoUploadService.controller.DiscoveryclientController;
import cc.app.microservice.VideoUploadService.data.JwtUtil;
import cc.app.microservice.VideoUploadService.filter.JwtRequestFilter;
import cc.app.microservice.VideoUploadService.util.DiscoveryUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class VideoUploadServiceApplication {

	private static final String APP_NAME = "VideoUploadService";
	private static String discAddress;
	private static String databaseIP;
	private static String hostIP;

	@Bean
	public RestTemplate restTemplate(){
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

	@Bean
	public DiscoveryUtil discUtil() {
		return new DiscoveryUtil();
	}

	public static String getDiscAddress(){
		return discAddress;
	}

	public static String getHostIP() {
		return hostIP;
	}

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(VideoUploadServiceApplication.class);

		Map<String, Object> conf = new HashMap<String, Object>();

		if(args.length != 4) {
			System.out.println("Usage: java -jar filename.jar <portNo> <discovery server IP>:<portNo> <databaseIP> <hostIP>");
			return;
		}

		String PORT = args[0];
		discAddress = args[1];
		databaseIP = args[2];
		hostIP = args[3];

		conf.put("server.port", PORT);
		conf.put("spring.application.name", APP_NAME);
		conf.put("spring.datasource.jdbcUrl", "jdbc:mysql://"+ databaseIP +":3306/UploadService");
		conf.put("spring.second-db.jdbcUrl", "jdbc:mysql://"+ databaseIP +":3306/AuthenticationService");

		app.setDefaultProperties(conf);
		app.run(args);

		DiscoveryclientController controller = new DiscoveryclientController(APP_NAME, discAddress);
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
