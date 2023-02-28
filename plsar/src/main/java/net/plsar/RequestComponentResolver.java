package net.plsar;

import net.plsar.model.Component;
import net.plsar.model.FileComponent;
import net.plsar.model.NetworkRequest;
import net.plsar.model.RequestComponent;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RequestComponentResolver {

    byte[] requestBytes;
    NetworkRequest networkRequest;

    public void resolve(){
        Map<String, String> headers = networkRequest.getHeaders();
        String contentType = headers.get("content-type");
        String[] boundaryParts = contentType != null ? contentType.split("boundary=") : new String[]{};

        if (boundaryParts.length > 1) {
            String formDelimiter = boundaryParts[1];
            String requestPayload = getRequestContent(requestBytes);
            List<RequestComponent> requestComponents = getRequestComponents(formDelimiter, requestPayload);
            requestComponents.forEach(requestComponent -> {
                String requestComponentKey = requestComponent.getName();
                networkRequest.setRequestComponent(requestComponentKey, requestComponent);
            });
        }else if(requestBytes.length > 0){

            try {

                String queryBytes = new String(requestBytes, "utf-8");
                String requestQueryComplete = java.net.URLDecoder.decode(queryBytes, StandardCharsets.UTF_8.name());
                String[] requestQueryParts = requestQueryComplete.split("\r\n\r\n", 2);
                if(requestQueryParts.length == 2 &&
                        !requestQueryParts[1].equals("")) {
                    String requestQuery =  requestQueryParts[1];
                    for (String entry : requestQuery.split("&")) {
                        RequestComponent requestComponent = new RequestComponent();
                        String[] keyValue = entry.split("=", 2);
                        String key = keyValue[0];
                        if (keyValue.length > 1) {
                            String value = keyValue[1];
                            requestComponent.setName(key);
                            requestComponent.setValue(value);
                        } else {
                            requestComponent.setName(key);
                            requestComponent.setValue("");
                        }
                        networkRequest.put(key, requestComponent);
                    }
                }

            } catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }

    List<RequestComponent> getRequestComponents(String delimeter, String requestPayload){
        String elementRegex = "(Content-Disposition: form-data; name=\"[a-zA-Z\\-\\._\\d]+\"\\s)|(Content-Disposition: form-data; name=\"[a-zA-Z\\-\\.\\d]+\"; filename=\"[a-zA-Z\\.\\-_\\s\\d\\']+\")";

        List<Component> components = new ArrayList<>();

        Pattern elementPattern = Pattern.compile(elementRegex);
        Matcher elementMatcher = elementPattern.matcher(requestPayload);

        Integer lastIndex = 0;

        while (elementMatcher.find()) {
            String fileGroup = elementMatcher.group();
            Integer beginIndex = requestPayload.indexOf(fileGroup, lastIndex);
            Integer delimiterIndex = requestPayload.indexOf(delimeter, beginIndex + 1);
            String componentContent = requestPayload.substring(beginIndex, delimiterIndex);
            Component component = new Component(componentContent);
            component.setActiveBeginIndex(beginIndex);
            component.setActiveCloseIndex(delimiterIndex + delimeter.length());
            components.add(component);
            lastIndex = delimiterIndex;
        }

        final String NAME = "name=\"";
        final String FILE = "filename=\"";
        final String NEWLINE = "\r\n";
        Map<String, RequestComponent> requestComponentMap = new HashMap<>();
        for(Component component : components){
            String componentContent = component.getComponent();
            Integer beginNameIdx = componentContent.indexOf(NAME);
            Integer endNameIdx = componentContent.indexOf("\"", beginNameIdx + NAME.length());
            String nameElement = componentContent.substring(beginNameIdx + NAME.length(), endNameIdx);

            Integer beginFilenameIdx = componentContent.indexOf(FILE);
            if(beginFilenameIdx.compareTo(-1) == 0){
                Integer beginValueIdx = componentContent.indexOf("\r\n\r\n", endNameIdx);
                Integer endValueIdx = componentContent.indexOf("--", beginValueIdx + NEWLINE.length());
                String valueDirty = componentContent.substring(beginValueIdx + NEWLINE.length(), endValueIdx);
                String value = valueDirty.replace("\r\n", "");

                if(requestComponentMap.containsKey(nameElement)){
                    RequestComponent requestComponent = requestComponentMap.get(nameElement);
                    requestComponent.setName(nameElement);
                    requestComponent.setValue(value);
                    requestComponent.getValues().add(value);
                    requestComponentMap.replace(nameElement, requestComponent);
                }else{
                    RequestComponent requestComponent = new RequestComponent();
                    requestComponent.setName(nameElement);
                    requestComponent.setValue(value);
                    requestComponent.getValues().add(value);
                    requestComponentMap.put(nameElement, requestComponent);
                }

            }else{
                if(requestComponentMap.containsKey(nameElement)){
                    RequestComponent requestComponent = requestComponentMap.get(nameElement);
                    requestComponent.setName(nameElement);
                    FileComponent fileComponent = getFileComponent(component, componentContent);
                    if(fileComponent != null) {
                        requestComponent.getFileComponents().add(fileComponent);
                        requestComponentMap.put(nameElement, requestComponent);
                    }
                }else{
                    RequestComponent requestComponent = new RequestComponent();
                    requestComponent.setName(nameElement);
                    FileComponent fileComponent = getFileComponent(component, componentContent);
                    if(fileComponent != null) {
                        requestComponent.getFileComponents().add(fileComponent);
                        requestComponentMap.put(nameElement, requestComponent);
                    }
                }
            }
        }

        List<RequestComponent> requestComponents = new ArrayList<>();
        for(Map.Entry<String, RequestComponent> requestComponentEntry: requestComponentMap.entrySet()){
            requestComponents.add(requestComponentEntry.getValue());
        }

        return requestComponents;
    }

    protected FileComponent getFileComponent(Component component, String componentContent) {
        FileComponent fileComponent = new FileComponent();

        Integer fileIdx = componentContent.indexOf("filename=");
        Integer startFile = componentContent.indexOf("\"", fileIdx + 1);
        Integer endFile = componentContent.indexOf("\"", startFile + 1);
        String fileName = componentContent.substring(startFile + 1, endFile);
        fileComponent.setFileName(fileName);

        Integer startContent = componentContent.indexOf("Content-Type", endFile + 1);
        Integer startType = componentContent.indexOf(":", startContent + 1);
        Integer endType = componentContent.indexOf("\r\n", startType + 1);
        String type = componentContent.substring(startType + 1, endType).trim();
        fileComponent.setContentType(type);

        Integer activeBeginIndex = component.getActiveBeginIndex() + componentContent.indexOf("\r\n", endType) + "\r\n\r\n".length();
        Integer activeCloseIndex = component.getActiveCloseIndex() + componentContent.indexOf("--", activeBeginIndex);

        if(activeCloseIndex >= requestBytes.length)activeCloseIndex = requestBytes.length;

        if (activeCloseIndex - activeBeginIndex > "\r\n\r\n".length()) {

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            for (int activeIndex = activeBeginIndex; activeIndex < activeCloseIndex; activeIndex++) {
                byte activeByte = requestBytes[activeIndex];
                byteArrayOutputStream.write(activeByte);
            }

            byte[] bytes = byteArrayOutputStream.toByteArray();
            fileComponent.setFileBytes(bytes);
            fileComponent.setActiveIndex(activeCloseIndex);

            return fileComponent;
        }

        return null;
    }

    public byte[] getRequestBytes() {
        return requestBytes;
    }

    public void setRequestBytes(byte[] requestBytes) {
        this.requestBytes = requestBytes;
    }

    public NetworkRequest getNetworkRequest() {
        return networkRequest;
    }

    public void setNetworkRequest(NetworkRequest networkRequest) {
        this.networkRequest = networkRequest;
    }

    String getRequestContent(byte[] requestBytes){
        StringBuilder sb = new StringBuilder();
        for (byte b : requestBytes) {
            sb.append((char) b);
        }
        return  sb.toString();
    }

}