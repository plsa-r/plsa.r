package net.plsar;

import java.util.Map;

public class RouteResult {

    Boolean completeRequest;
    byte[] responseBytes;
    String contentType;
    String responseCode;

    Map<String, Object> redirectAttributes;

    public Boolean getCompleteRequest() {
        return completeRequest;
    }

    public void setCompleteRequest(Boolean completeRequest) {
        this.completeRequest = completeRequest;
    }

    public byte[] getResponseBytes() {
        return responseBytes;
    }

    public void setResponseBytes(byte[] responseBytes) {
        this.responseBytes = responseBytes;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public Map<String, Object> getRedirectAttributes() {
        return redirectAttributes;
    }

    public void setRedirectAttributes(Map<String, Object> redirectAttributes) {
        this.redirectAttributes = redirectAttributes;
    }

    public RouteResult(Boolean completeRequest) {
        this.completeRequest = completeRequest;
    }

    public RouteResult(Map<String, Object> redirectAttributes) {
        this.redirectAttributes = redirectAttributes;
    }

    public RouteResult(byte[] responseBytes, String responseCode, String contentType) {
        this.responseBytes = responseBytes;
        this.responseCode = responseCode;
        this.contentType = contentType;
    }

    public RouteResult() { }

}
