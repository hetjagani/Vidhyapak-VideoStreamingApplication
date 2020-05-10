package cc.app.microservice.UIService.util;

import cc.app.microservice.UIService.UiServiceApplication;
import cc.app.microservice.UIService.model.DiscoveryInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

public class DiscoveryUtil {

    private String discAddress = UiServiceApplication.getDiscAddress();

    @Autowired
    private RestTemplate restTemplate;

    public String getServiceAddress(String serviceName) throws ResponseStatusException {

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
