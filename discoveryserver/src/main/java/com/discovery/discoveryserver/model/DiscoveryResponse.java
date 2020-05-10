package com.discovery.discoveryserver.model;

public class DiscoveryResponse {
    private String responseMessage;
    private boolean heartbeatReceived;

    public DiscoveryResponse () {}

    public DiscoveryResponse(String responseMessage, boolean heartbeatReceived) {
        this.responseMessage = responseMessage;
        this.heartbeatReceived = heartbeatReceived;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public boolean isHeartbeatReceived() {
        return heartbeatReceived;
    }

    public void setHeartbeatReceived(boolean heartbeatReceived) {
        this.heartbeatReceived = heartbeatReceived;
    }
}
