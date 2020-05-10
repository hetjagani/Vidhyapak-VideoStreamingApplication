package cc.app.microservice.VideoUploadService.util;

import cc.app.microservice.VideoUploadService.model.DiscoveryInfo;
import cc.app.microservice.VideoUploadService.VideoUploadServiceApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

public class DiscoveryUtil {

    private String discAddress = VideoUploadServiceApplication.getDiscAddress();

    @Autowired
    private RestTemplate restTemplate;

    public String getServiceAddress(String serviceName) {

        String discServURI = "http://" + discAddress + "/discovery/serviceinfo/" + serviceName;
        DiscoveryInfo info = null;
        try {
            info = restTemplate.getForObject(discServURI, DiscoveryInfo.class);
        } catch (Exception ex) {
           ex.printStackTrace();
        }

        return "http://"+info.getIpAddr() + ":" + info.getServicePort();
    }
}
