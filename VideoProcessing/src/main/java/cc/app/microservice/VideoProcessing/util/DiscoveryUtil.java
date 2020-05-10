package cc.app.microservice.VideoProcessing.util;

import cc.app.microservice.VideoProcessing.VideoProcessingApplication;
import cc.app.microservice.VideoProcessing.model.DiscoveryInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

public class DiscoveryUtil {

    private String discAddress = VideoProcessingApplication.getDiscAddress();

    private RestTemplate restTemplate = new RestTemplate();

    public String getServiceAddress(String serviceName) throws HttpClientErrorException {

        try {
            String discServURI = "http://" + discAddress + "/discovery/serviceinfo/" + serviceName;
            DiscoveryInfo info = null;
            info = restTemplate.getForObject(discServURI, DiscoveryInfo.class);
            return "http://" + info.getIpAddr() + ":" + info.getServicePort();
        }catch (ResourceAccessException ex) {
            System.out.println("Discovery service not yet started");
            return null;
        }
    }
}
