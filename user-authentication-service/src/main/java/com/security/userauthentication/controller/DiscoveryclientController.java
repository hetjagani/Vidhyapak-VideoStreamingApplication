package com.security.userauthentication.controller;

import com.security.userauthentication.model.DiscoveryInfo;
import com.security.userauthentication.model.DiscoveryResponse;
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

    public DiscoveryclientController(String applicationName, int port, String discIp) {
        String ipAddr = null;
        heartbeatURL = "http://"+discIp+"/discovery/heartbeat";
        try {
            Enumeration interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface i = (NetworkInterface) interfaces.nextElement();
                if(i.getName().matches("eth0")){
                    Enumeration addresses = i.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        InetAddress address = (InetAddress) addresses.nextElement();
                        ipAddr = address.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        this.serviceInfo = new DiscoveryInfo(ipAddr, applicationName, port);
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
