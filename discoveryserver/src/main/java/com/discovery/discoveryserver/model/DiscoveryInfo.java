package com.discovery.discoveryserver.model;

import java.util.Objects;

public class DiscoveryInfo {
    String ipAddr;
    String serviceName;
    int servicePort;

    public DiscoveryInfo() {}

    public DiscoveryInfo(String ipAddr, String serviceName) {
        this.ipAddr = ipAddr;
        this.serviceName = serviceName;
    }

    public DiscoveryInfo(String ipAddr, String serviceName, int servicePort) {
        this.ipAddr = ipAddr;
        this.serviceName = serviceName;
        this.servicePort = servicePort;
    }

    public int getServicePort() {
        return servicePort;
    }

    public void setServicePort(int servicePort) {
        this.servicePort = servicePort;
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    @Override
    public String toString() {
        return "DiscoveryInfo{" +
                "ipAddr='" + ipAddr + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", servicePort=" + servicePort +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DiscoveryInfo that = (DiscoveryInfo) o;
        return servicePort == that.servicePort &&
                ipAddr.equals(that.ipAddr) &&
                serviceName.equals(that.serviceName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ipAddr, serviceName, servicePort);
    }

    public boolean valid() {
        return this.ipAddr != null ||
                this.serviceName != "" ||
                this.servicePort != 0 ||
                this.serviceName != null ||
                this.ipAddr != "";
    }
}
