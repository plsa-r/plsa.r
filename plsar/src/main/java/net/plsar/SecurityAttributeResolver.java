package net.plsar;

import net.plsar.model.NetworkRequest;
import net.plsar.model.NetworkResponse;
import net.plsar.model.SecurityAttribute;
import net.plsar.model.UserCredential;
import net.plsar.security.SecurityAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

public class SecurityAttributeResolver {
    final String SECURITY_KEY = "cookie";
    SecurityAttributes securityAttributes;

    public Boolean resolve(NetworkRequest networkRequest, NetworkResponse networkResponse) {
        String securityAttributesElement = networkRequest.getHeaders().get(SECURITY_KEY);
        try {
            String[] securityAttributePartials = securityAttributesElement.split(";");
            for (String securityAttributePartial : securityAttributePartials) {
                
                String[] securityAttributeParts = securityAttributePartial.split("=", 2);

                String securityAttributeKey = securityAttributeParts[0].trim();
                String securityAttributeValue = securityAttributeParts[1].trim();

                if (securityAttributes.getSecurityElement().equals(securityAttributeKey)) {

                    String[] securityAttributeValueElements = securityAttributeValue.split("\\.");
                    String securedElement = securityAttributeValueElements[0];

                    if(!securedElement.equals(securityAttributes.getSecuredAttribute()))continue;

                    String securityElementPrincipalPre = securityAttributeValueElements[1];

                    String securityElementValue = securedElement + "." + securityElementPrincipalPre + "; path=/";
                    SecurityAttribute securityAttribute = new SecurityAttribute(securityAttributeKey, securityElementValue);

                    networkResponse.getSecurityAttributes().remove("plsar.security");
                    networkResponse.getSecurityAttributes().put("plsar.security", securityAttribute);

                    byte[] securityElementPrincipalBytes = Base64.getDecoder().decode(securityElementPrincipalPre);
                    String securityElementPrincipal = new String(securityElementPrincipalBytes);

                    networkRequest.setSecurityAttributeInfo(securityAttributes.getSecuredAttribute());
                    networkRequest.setUserCredential(securityElementPrincipal);
                }
            }
        }catch(Exception ex){}

        return true;
    }

    public SecurityAttributes getSecurityAttributes() {
        return securityAttributes;
    }

    public void setSecurityAttributes(SecurityAttributes securityAttributes) {
        this.securityAttributes = securityAttributes;
    }

    public long getCurrentTime(){
        LocalDateTime ldt = LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String date = dtf.format(ldt);
        return Long.parseLong(date);
    }


}
