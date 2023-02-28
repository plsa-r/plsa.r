package net.plsar;

import net.plsar.model.NetworkRequest;

public class NetworkRequestHeaderResolver {
    final String BREAK = "\r\n";

    String headerElement;
    NetworkRequest networkRequest;

    public void resolve(){
        String[] headerComponents = headerElement.split(BREAK);
        for(String headerLine : headerComponents){
            String[] headerLineComponents = headerLine.split(":");
            if(headerLineComponents.length == 2) {
                String fieldKey = headerLineComponents[0].trim();
                String fieldContent = headerLineComponents[1].trim();
                networkRequest.getHeaders().put(fieldKey.toLowerCase(), fieldContent);
            }
        }
    }

    public void setRequestHeaderElement(String headerElement) {
        this.headerElement = headerElement;
    }

    public void setNetworkRequest(NetworkRequest networkRequest) {
        this.networkRequest = networkRequest;
    }
}
