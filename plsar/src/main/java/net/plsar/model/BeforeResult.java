package net.plsar.model;

public class BeforeResult {
    public BeforeResult(String redirectUri) {
        this.message = "";
        this.redirectUri = redirectUri;
    }
    public BeforeResult(){
        this.message = "";
        this.redirectUri = "";
    }

    String redirectUri;
    String message;

    public String getRedirectUri() {
        if(redirectUri == null) return "";
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public String getMessage() {
        if(message == null) return "";
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
