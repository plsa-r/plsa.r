package net.plsar.model;

public class UserCredential {
    public UserCredential(String credential){
        this.credential = credential;
    }
    String credential;

    public String getCredential() {
        return credential;
    }

    public void setCredential(String credential) {
        this.credential = credential;
    }
}
