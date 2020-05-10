package com.discovery.discoveryserver;

import com.discovery.discoveryserver.model.DiscoveryInfo;
import com.discovery.discoveryserver.model.DiscoveryResponse;
import com.discovery.discoveryserver.util.DiscoveryInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/discovery")
public class DiscoveryserverController {

    @Autowired
    private DiscoveryInfoRepository discoveryRepo;

    @PostMapping(path="/heartbeat", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public DiscoveryResponse registerService(@RequestBody DiscoveryInfo info) {
        ConcurrentHashMap<DiscoveryInfo, Boolean> map = DiscoveryserverApplication.getServicesMap();
        if(!info.valid()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        if(discoveryRepo.isRegistered(info)){
            // mark as heartbeat received
            map.replace(info, true);
        }else {
            // register this service
            map.putIfAbsent(info, true);
            discoveryRepo.save(info);
        }

        DiscoveryserverApplication.setServicesMap(map);

        return new DiscoveryResponse("ACCEPTED",true);
    }

    @GetMapping(path = "/serviceinfo/{name}")
    public ResponseEntity<?> getIpAddress(@PathVariable("name") String serviceName){
        // Get oldest discovery ip address
        Optional<DiscoveryInfo> optInfo = discoveryRepo.getOldestDiscovery(serviceName);

        if(!optInfo.isEmpty()){
            discoveryRepo.updateTimestamp(optInfo.get());
            return ResponseEntity.ok(optInfo.get());
        }
        else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Service name provided is not found.");
        }
    }

    @GetMapping(path = "/registeredservices")
    public List<DiscoveryInfo> getAllRegisteredServices() {
        return discoveryRepo.getAll();
    }

    @PostMapping(path="/remove")
    public int remove(@RequestBody DiscoveryInfo info) {
        return discoveryRepo.remove(info);
    }

    public void checkServicesStatus() {
        RestTemplate template = new RestTemplate();

        ConcurrentHashMap<DiscoveryInfo, Boolean> map = DiscoveryserverApplication.getServicesMap();

        for (DiscoveryInfo disc : map.keySet()) {
            //if heartbeat not received
            if(map.get(disc) == false){
                map.remove(disc);
                // Remove from database
                template.postForObject("http://localhost:8080/discovery/remove",disc, Integer.class);
            }
        }
        map.replaceAll((key, oldval) -> false);

        DiscoveryserverApplication.setServicesMap(map);

        System.out.println("[CheckServiceStatus] Log");
        System.out.println(map.toString());
    }

}
