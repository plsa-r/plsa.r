package net.plsar.model;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class BeforeResult {
    public BeforeResult(String redirectUri) {
        this.message = "";
        this.redirectUri = redirectUri;
        this.attributes = new ConcurrentHashMap<>();
    }
    public BeforeResult(){
        this.message = "";
        this.redirectUri = "";
        this.attributes = new ConcurrentHashMap<>();
    }

    String message;
    String redirectUri;
    ConcurrentMap<String, Object> attributes;

    public Object get(String key){
        if(attributes.containsKey(key)){
            return attributes.get(key);
        }
        return null;
    }

    public void set(String key, Object value){
        if(!attributes.containsKey(key)){
            attributes.put(key, value);
        }
    }

    public String getMessage() {
        if(message == null) return "";
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRedirectUri() {
        if(redirectUri == null) return "";
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public ConcurrentMap<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(ConcurrentMap<String, Object> attributes) {
        this.attributes = attributes;
    }
}
