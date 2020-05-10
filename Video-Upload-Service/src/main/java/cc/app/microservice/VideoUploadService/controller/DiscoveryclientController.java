package cc.app.microservice.VideoUploadService.controller;

import cc.app.microservice.VideoUploadService.VideoUploadServiceApplication;
import cc.app.microservice.VideoUploadService.model.DiscoveryInfo;
import cc.app.microservice.VideoUploadService.model.DiscoveryResponse;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class DiscoveryclientController {

    private RestTemplate restTemplate = new RestTemplate();

    private String heartbeatURL;

    private DiscoveryInfo serviceInfo;

    public DiscoveryclientController(String applicationName, String discIp) {
        heartbeatURL = "http://"+discIp+"/discovery/heartbeat";

        this.serviceInfo = new DiscoveryInfo(VideoUploadServiceApplication.getHostIP(), applicationName, 5000);
    }

    public void sendHeartBeat() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            DiscoveryResponse response = restTemplate.postForObject(heartbeatURL, serviceInfo, DiscoveryResponse.class);
            if(response.isHeartbeatReceived()) System.out.println("Heartbeat sent");
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        } catch (ResourceAccessException ex) {
            ex.printStackTrace();
        }
//        System.out.println("[Sent object]: "+ serviceInfo.toString());
//        System.out.println("Function executed...\n");
    }

}
