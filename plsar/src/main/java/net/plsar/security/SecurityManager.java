package net.plsar.security;

import net.plsar.model.NetworkRequest;
import net.plsar.model.NetworkResponse;
import net.plsar.model.SecurityAttribute;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Set;

public class SecurityManager {
    public SecurityManager(SecurityAccess securityAccess) {
        this.securityAccess = securityAccess;
    }

    public SecurityManager(SecurityAccess securityAccess, SecurityAttributes securityAttributes){
        this.securityAccess = securityAccess;
        this.securityAttributes = securityAttributes;
    }

    SecurityAccess securityAccess;
    SecurityAttributes securityAttributes;

    public SecurityAttributes getSecurityAttributes() {
        return securityAttributes;
    }

    public void setSecurityAttributes(SecurityAttributes securityAttributes) {
        this.securityAttributes = securityAttributes;
    }

    public SecurityAccess getSecurityAccess() {
        return securityAccess;
    }

    public void setSecurityAccess(SecurityAccess securityAccess) {
        this.securityAccess = securityAccess;
    }

    public boolean hasRole(String role, NetworkRequest networkRequest){
        String user = getUser(networkRequest);
        if(user != null) {
            Set<String> roles = securityAccess.getRoles(user);
            if(roles.contains(role)){
                return true;
            }
        }
        return false;
    }

    public boolean hasPermission(String permission, NetworkRequest networkRequest){
        String user = networkRequest.getUserCredential();
        if(user != null) {
            Set<String> permissions = securityAccess.getPermissions(user);
            if(permissions.contains(permission)){
                return true;
            }
        }
        return false;
    }

    public String getUser(NetworkRequest networkRequest){
        return networkRequest.getUserCredential();
    }

    public Boolean signin(String username, String passwordUntouched, NetworkRequest networkRequest, NetworkResponse networkResponse) {
        String hashed = hash(passwordUntouched);
        String password = securityAccess.getPassword(username);

        try{
            if (!isAuthenticated(networkRequest) &&
                    password.equals(hashed)) {

                String securityAttributePrincipal = Base64.getEncoder().encodeToString(username.getBytes());
                networkRequest.setSecurityAttributeInfo(securityAttributes.getSecuredAttribute());
                String securityAttributeValue = securityAttributes.getSecuredAttribute() + "." + securityAttributePrincipal + "; path=/;";
//                System.out.println("signin: " + securityAttributeValue);
                SecurityAttribute securityAttribute = new SecurityAttribute(securityAttributes.getSecurityElement(), securityAttributeValue);
                networkResponse.getSecurityAttributes().put("plsar.security", securityAttribute);
                return true;
            }

        }catch(Exception ex){
            ex.printStackTrace();
        }

        return false;
    }

    public boolean signout(NetworkRequest networkRequest, NetworkResponse networkResponse){
        networkRequest.setSecurityAttributeInfo("");
        String securityAttributeValue = ";Expires/MaxAge=-1;Expires=-1;MaxAge=-1;";
        SecurityAttribute securityAttribute = new SecurityAttribute(securityAttributes.getSecurityElement(), securityAttributeValue);
        networkResponse.getSecurityAttributes().remove("plsar.security");
        networkResponse.getSecurityAttributes().put("plsar.security", securityAttribute);
        return true;
    }

    public boolean isAuthenticated(NetworkRequest networkRequest){
        if(!networkRequest.getUserCredential().equals("")){
            return true;
        }
        return false;
    }

    public String hash(String password){
        MessageDigest md;
        StringBuffer passwordHashed = new StringBuffer();

        try {
            md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes());
            byte byteData[] = md.digest();

            for (int i = 0; i < byteData.length; i++) {
                passwordHashed.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return passwordHashed.toString();
    }

    public static String dirty(String password){
        MessageDigest md;
        StringBuffer passwordHashed = new StringBuffer();

        try {
            md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes());
            byte byteData[] = md.digest();

            for (int i = 0; i < byteData.length; i++) {
                passwordHashed.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return passwordHashed.toString();
    }
}
