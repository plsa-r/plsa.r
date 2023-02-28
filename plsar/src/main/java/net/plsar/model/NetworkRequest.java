package net.plsar.model;

import net.plsar.RouteAttributes;
import net.plsar.security.SecurityAttributes;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class NetworkRequest {
    public NetworkRequest() {
        this.headers = new HashMap<>();
        this.requestComponents = new HashMap<>();
    }

    boolean redirect;
    String redirectLocation;
    String requestPath;
    String requestAction;
    String requestBody;
    Map<String, String> headers;
    Map<String, RequestComponent> requestComponents;
    RouteAttributes routeAttributes;
    String securityAttributeInfo;
    SecurityAttributes securityAttributes;
    String userCredential;

    public boolean isRedirect() {
        return redirect;
    }

    public void setRedirect(boolean redirect) {
        this.redirect = redirect;
    }

    public String getRedirectLocation() {
        return redirectLocation;
    }

    public void setRedirectLocation(String redirectLocation) {
        this.redirectLocation = redirectLocation;
    }

    public String getRequestPath() {
        return requestPath;
    }

    public void setRequestPath(String requestPath) {
        this.requestPath = requestPath;
    }

    public String getRequestAction() {
        return requestAction;
    }

    public void setRequestAction(String requestAction) {
        this.requestAction = requestAction;
    }

    public void addHeader(String fieldKey, String headerValue){
        this.headers.put(fieldKey, headerValue);
    }

    public void removeHeader(String fieldKey) {
        this.headers.remove(fieldKey);
    }

    public String getHeader(String fieldKey){
        if(headers.containsKey(fieldKey)){
            return headers.get(fieldKey);
        }
        return null;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public void put(String elementName, RequestComponent requestComponent){
        this.requestComponents.put(elementName, requestComponent);
    }

    public String getSecurityAttributeInfo() {
        if(securityAttributeInfo == null)return "";
        return securityAttributeInfo;
    }

    public void setSecurityAttributeInfo(String securityAttributeInfo) {
        this.securityAttributeInfo = securityAttributeInfo;
    }

    public void setRequestComponents(Map<String, RequestComponent> requestComponents) {
        this.requestComponents = requestComponents;
    }

    public void setRequestComponent(String key, RequestComponent requestComponent){
        this.requestComponents.put(key, requestComponent);
    }

    public String getValue(String key){
        if(requestComponents.containsKey(key)){
            return requestComponents.get(key).getValue();
        }
        return null;
    }

    public RequestComponent getRequestComponent(String key){
        if(requestComponents.containsKey(key)){
            return requestComponents.get(key);
        }
        return null;
    }

    public List<RequestComponent> getRequestComponents(){
        List<RequestComponent> components = new ArrayList<>();
        for(Map.Entry<String, RequestComponent> requestComponentEntry : requestComponents.entrySet()){
            components.add(requestComponentEntry.getValue());
        }
        return components;
    }

    public RouteAttributes getRouteAttributes() {
        return routeAttributes;
    }

    public void setRouteAttributes(RouteAttributes routeAttributes) {
        this.routeAttributes = routeAttributes;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public SecurityAttributes getSecurityAttributes() {
        return securityAttributes;
    }

    public void setSecurityAttributes(SecurityAttributes securityAttributes) {
        this.securityAttributes = securityAttributes;
    }

    public String getUserCredential() {
        if(userCredential == null) return "";
        return userCredential;
    }

    public void setUserCredential(String userCredential) {
        this.userCredential = userCredential;
    }

    public void removeRedirect(){
        this.redirect = false;
        this.redirectLocation = null;
    }

    public void setValues(String parameters) {
        String[] keyValues = parameters.split("&");
        for(String keyValue : keyValues){
            String[] parts = keyValue.split("=");
            if(parts.length > 1){
                String key = parts[0];
                String value = parts[1];
                RequestComponent requestComponent = new RequestComponent();
                requestComponent.setName(key);
                requestComponent.setValue(value);
                requestComponents.put(key, requestComponent);
            }
        }
    }

    public <T> T get(Class<?> klass){
        Object classInstance =  null;
        try {
            classInstance = klass.getConstructor().newInstance();
            Field[] classInstanceFields = klass.getDeclaredFields();
            for(Field classInstanceField : classInstanceFields){
                String fieldName = classInstanceField.getName();
                String fieldValue = getValue(fieldName);
                if(fieldValue != null &&
                        !fieldValue.equals("")){

                    classInstanceField.setAccessible(true);
                    Type fieldType = classInstanceField.getType();

                    if (fieldType.getTypeName().equals("int") ||
                            fieldType.getTypeName().equals("java.lang.Integer")) {
                        classInstanceField.set(classInstance, Integer.valueOf(fieldValue));
                    }
                    if (fieldType.getTypeName().equals("double") ||
                            fieldType.getTypeName().equals("java.lang.Double")) {
                        classInstanceField.set(classInstance, Double.valueOf(fieldValue));
                    }
                    if (fieldType.getTypeName().equals("float") ||
                            fieldType.getTypeName().equals("java.lang.Float")) {
                        classInstanceField.set(classInstance, Float.valueOf(fieldValue));
                    }
                    if (fieldType.getTypeName().equals("long") ||
                            fieldType.getTypeName().equals("java.lang.Long")) {
                        classInstanceField.set(classInstance, Long.valueOf(fieldValue));
                    }
                    if (fieldType.getTypeName().equals("boolean") ||
                            fieldType.getTypeName().equals("java.lang.Boolean")) {
                        classInstanceField.set(classInstance, Boolean.valueOf(fieldValue));
                    }
                    if (fieldType.getTypeName().equals("java.math.BigDecimal")) {
                        classInstanceField.set(classInstance, new BigDecimal(fieldValue));
                    }
                    if (fieldType.getTypeName().equals("java.lang.String")) {
                        classInstanceField.set(classInstance, fieldValue);
                    }
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }

        return (T) klass.cast(classInstance);
    }

}
