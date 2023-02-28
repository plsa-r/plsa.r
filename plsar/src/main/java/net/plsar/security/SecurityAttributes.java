package net.plsar.security;

public class SecurityAttributes {
    public SecurityAttributes(String securityElement, String securedAttribute) {
        this.securityElement = securityElement;
        this.securedAttribute = securedAttribute;
    }

    String securityElement;
    String defaultAttribute;
    String securedAttribute;

    public String getSecurityElement() {
        return securityElement;
    }

    public void setSecurityElement(String securityElement) {
        this.securityElement = securityElement;
    }

    public String getDefaultAttribute() {
        return defaultAttribute;
    }

    public void setDefaultAttribute(String defaultAttribute) {
        this.defaultAttribute = defaultAttribute;
    }

    public String getSecuredAttribute() {
        return securedAttribute;
    }

    public void setSecuredAttribute(String securedAttribute) {
        this.securedAttribute = securedAttribute;
    }
}
