package net.plsar.model;

public class SecurityAttribute {
    String name;
    String value;
    String expires;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getExpires() {
        return expires;
    }

    public void setExpires(String expires) {
        this.expires = expires;
    }

    public SecurityAttribute(String name, String value) {
        this.name = name;
        this.value = value;
    }
}
