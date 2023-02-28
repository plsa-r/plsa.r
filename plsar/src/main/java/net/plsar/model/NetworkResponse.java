package net.plsar.model;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetworkResponse {
    public NetworkResponse(){
        this.securityAttributes = new HashMap<>();
    }

    boolean redirect;
    String contentType;
    Map<String, SecurityAttribute> securityAttributes;
    OutputStream responseStream;

    public boolean isRedirect() {
        return redirect;
    }

    public void setRedirect(boolean redirect) {
        this.redirect = redirect;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Map<String, SecurityAttribute> getSecurityAttributes() {
        return securityAttributes;
    }

    public void setSecurityAttributes(Map<String, SecurityAttribute> securityAttributes) {
        this.securityAttributes = securityAttributes;
    }

    public OutputStream getResponseStream() {
        return responseStream;
    }

    public void setResponseStream(OutputStream responseStream) {
        this.responseStream = responseStream;
    }

}

