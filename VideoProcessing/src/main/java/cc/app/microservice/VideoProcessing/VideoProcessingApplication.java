package cc.app.microservice.VideoProcessing;

import cc.app.microservice.VideoProcessing.controller.DiscoveryclientController;
import cc.app.microservice.VideoProcessing.task.ProcessingTask;
import cc.app.microservice.VideoProcessing.util.DiscoveryUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class VideoProcessingApplication {

	private static final String APP_NAME = "VideoProcessingService";
	private static String discAddress;
	private static String scriptPath;

	@Bean
	public RestTemplate restTemplate()
	{
		return new RestTemplate();
	}

	@Bean
	public DiscoveryUtil discUtil() {
		return new DiscoveryUtil();
	}

	public static String getDiscAddress(){
		return discAddress;
	}

	public static String getScriptPath() { return scriptPath; }

	// DEPENDENCIES:
	// 	* ffmpeg with all encoders build
	//	* mediainfo library
	public static void main(String[] args) {

		if(args.length != 3) {
			System.out.println("Usage: java -jar filename.jar <portNo> <discovery server IP>:<portNo> <script path>");
			return;
		}

		String PORT = args[0];
		discAddress = args[1];
		scriptPath = args[2];

//		ProcessingTask task = new ProcessingTask();

//		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
//		executor.scheduleAtFixedRate(task, 0, 15, TimeUnit.SECONDS);

		Thread taskProcessing = new Thread(new ProcessingTask());
		taskProcessing.start();

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
