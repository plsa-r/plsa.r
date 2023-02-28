package net.plsar;

import net.plsar.model.*;
import net.plsar.resources.StargzrResources;
import net.plsar.security.SecurityAttributes;
import net.plsar.security.SecurityManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;

public class NetworkRequestExecutor implements Runnable {

    String requestGuid;

    final String FAVICON = "/favicon.ico";
    final String BREAK = "\r\n";
    final String SPACE = " ";
    final String DOUBLEBREAK = "\r\n\r\n";

    final Integer REQUEST_METHOD = 0;
    final Integer REQUEST_PATH = 1;
    final Integer REQUEST_VERSION = 2;

    String RENDERER;

    FlashMessage flashMessage;
    ViewCache viewCache;
    ViewConfig viewConfig;

    ViewCache storedViewCache;
    FlashMessage storedFlashMessage;

    StargzrResources stargzrResources;

    String resourcesDirectory;
    Socket socketClient;
    ExecutorService executorService;
    ServerSocket serverSocket;
    List<Class<?>> viewRenderers;
    ConcurrentMap<String, byte[]> viewBytesMap;

    PersistenceConfig persistenceConfig;

    RouteAttributes routeAttributes;
    Class<?> securityAccessKlass;

    public NetworkRequestExecutor(String RENDERER, String resourcesDirectory, RouteAttributes routeAttributes, ViewConfig viewConfig, ConcurrentMap<String, byte[]> viewBytesMap, ExecutorService executorService, ServerSocket serverSocket, List<Class<?>> viewRenderers, PersistenceConfig persistenceConfig, Class<?> securityAccessKlass, FlashMessage storedFlashMessage, ViewCache storedViewCache) throws InterruptedException {
        this.stargzrResources = new StargzrResources();
        this.requestGuid = stargzrResources.getGuid(32);
        this.RENDERER = RENDERER;
        this.securityAccessKlass = securityAccessKlass;
        this.routeAttributes = routeAttributes;
        this.resourcesDirectory = resourcesDirectory;
        this.viewConfig = viewConfig;
        this.viewBytesMap = viewBytesMap;
        this.executorService = executorService;
        this.serverSocket = serverSocket;
        this.viewRenderers = viewRenderers;
        this.storedViewCache = storedViewCache;
        this.storedFlashMessage = storedFlashMessage;
        this.persistenceConfig = persistenceConfig;
    }

    @Override
    public void run() {

        NetworkRequestFactory networkRequestFactory = new NetworkRequestFactory();

        try {

            if(storedFlashMessage != null){
                this.flashMessage = storedFlashMessage;
            }else{
                this.flashMessage = new FlashMessage();
            }

            if(storedViewCache != null){
                this.viewCache = storedViewCache;
            }else{
                this.viewCache = new ViewCache();
            }

            socketClient = serverSocket.accept();
            Thread.sleep(19);

            InputStream requestInputStream = socketClient.getInputStream();
            OutputStream requestOutputStream = socketClient.getOutputStream();

            NegotiatorRegistryFactory negotiatorRegistryFactory = new NegotiatorRegistryFactory();
            negotiatorRegistryFactory.setPersistenceConfig(persistenceConfig);
            negotiatorRegistryFactory.setSecurityAccessKlass(securityAccessKlass);
            negotiatorRegistryFactory.setRouteAttributes(routeAttributes);
            negotiatorRegistryFactory.setServerResources(stargzrResources);

            RouteEndpointNegotiator routeEndpointNegotiator = negotiatorRegistryFactory.create();
            SecurityAttributes securityAttributes = networkRequestFactory.getSecurityAttributes();

            networkRequestFactory.setRenderer(RENDERER);
            networkRequestFactory.setServerSocket(serverSocket);
            networkRequestFactory.setViewRenderers(viewRenderers);
            networkRequestFactory.setViewConfig(viewConfig);
            networkRequestFactory.setViewBytesMap(viewBytesMap);
            networkRequestFactory.setResourcesDirectory(resourcesDirectory);
            networkRequestFactory.setRequestOutputStream(requestOutputStream);
            networkRequestFactory.setRequestInputStream(requestInputStream);
            networkRequestFactory.setExecutorService(executorService);
            networkRequestFactory.setSecurityAttributes(securityAttributes);
            networkRequestFactory.setViewCache(viewCache);
            networkRequestFactory.setFlashMessage(flashMessage);
            networkRequestFactory.setPersistenceConfig(persistenceConfig);
            networkRequestFactory.setRouteAttributes(routeAttributes);

            if(requestInputStream.available() == 0) {
                networkRequestFactory.execute();
                return;
            }

            RequestInputStreamReader requestInputStreamReader = new RequestInputStreamReader();
            requestInputStreamReader.setRequestInputStream(requestInputStream);
            requestInputStreamReader.read();
            ByteArrayOutputStream byteArrayOutputStream = requestInputStreamReader.getByteArrayOutputStream();

            String completeRequestContent = byteArrayOutputStream.toString();
            String[] requestBlocks = completeRequestContent.split(DOUBLEBREAK, 2);

            String requestHeaderElement = requestBlocks[0];
            String[] methodPathComponentsLookup = requestHeaderElement.split(BREAK);
            String methodPathComponent = methodPathComponentsLookup[0];

            String[] methodPathVersionComponents = methodPathComponent.split("\\s");

            String requestAction = methodPathVersionComponents[REQUEST_METHOD];
            String requestPath = methodPathVersionComponents[REQUEST_PATH];
            String requestVersion = methodPathVersionComponents[REQUEST_VERSION];

            if(requestPath.equals(FAVICON)){
                networkRequestFactory.execute();
                return;
            }

            NetworkRequest networkRequest = new NetworkRequest();
            networkRequest.setRequestAction(requestAction);
            networkRequest.setRequestPath(requestPath);
            NetworkResponse networkResponse = new NetworkResponse();

            String[] requestHeaderElements = requestHeaderElement.split(BREAK);
            for(String headerLineElement : requestHeaderElements){
                String[] headerLineComponents = headerLineElement.split(":", 2);
//                System.out.println("req=>" + networkRequest.getRequestPath() + "     /===> " + headerLineElement);
                if(headerLineComponents.length == 2) {
                    String fieldKey = headerLineComponents[0].trim();
                    String content = headerLineComponents[1].trim();
                    networkRequest.getHeaders().put(fieldKey.toLowerCase(), content);
                }
            }

            Integer attributesIdx = requestPath.indexOf("?");
            if(attributesIdx != -1) {
                String attributesElement = requestPath.substring(attributesIdx + 1);
                requestPath = requestPath.substring(0, attributesIdx);
                networkRequest.setValues(attributesElement);
                networkRequest.setRequestPath(requestPath);
            }


            SecurityAttributeResolver securityAttributeResolver = new SecurityAttributeResolver();
            securityAttributeResolver.setSecurityAttributes(routeEndpointNegotiator.getSecurityAttributes());
            securityAttributeResolver.resolve(networkRequest, networkResponse);

            networkResponse.setResponseStream(requestOutputStream);
            networkRequest.setSecurityAttributes(securityAttributes);

            byte[] requestByteArray = byteArrayOutputStream.toByteArray();
            RequestComponentResolver requestComponentResolver = new RequestComponentResolver();
            requestComponentResolver.setRequestBytes(requestByteArray);
            requestComponentResolver.setNetworkRequest(networkRequest);
            requestComponentResolver.resolve();

            RouteAttributes routeAttributes = routeEndpointNegotiator.getRouteAttributes();
            networkRequest.setRouteAttributes(routeAttributes);
            SecurityManager securityManager = routeAttributes.getSecurityManager();
            if(securityManager != null) securityManager.setSecurityAttributes(routeEndpointNegotiator.getSecurityAttributes());

            RouteResult routeResult = routeEndpointNegotiator.performNetworkRequest(RENDERER, resourcesDirectory, flashMessage, viewCache, viewConfig, networkRequest, networkResponse, securityAttributes, securityManager, viewRenderers, viewBytesMap);

            if(routeResult.getCompleteRequest() != null &&
                    routeResult.getCompleteRequest()){
                networkRequestFactory.execute();
                return;
            }

            StringBuilder sessionValues = new StringBuilder();

            NetworkRequestHeaderResolver networkRequestHeaderResolver = new NetworkRequestHeaderResolver();
            networkRequestHeaderResolver.setRequestHeaderElement(requestHeaderElement);
            networkRequestHeaderResolver.setNetworkRequest(networkRequest);
            networkRequestHeaderResolver.resolve();

            //todo:security attributes per route negotiator, secured:nothing
//            System.out.println("after:" + networkResponse.getSecurityAttributes().size());
            for(Map.Entry<String, SecurityAttribute> securityAttributeEntry : networkResponse.getSecurityAttributes().entrySet()){
                SecurityAttribute securityAttribute = securityAttributeEntry.getValue();
                sessionValues.append(securityAttribute.getName()).append("=").append(securityAttribute.getValue());
            }

            requestOutputStream.write((requestVersion + " ").getBytes());
            requestOutputStream.write(routeResult.getResponseCode().getBytes());
            requestOutputStream.write(BREAK.getBytes());

            if(networkRequest.isRedirect()) {
                requestOutputStream.write("Content-Type:text/html".getBytes());
                requestOutputStream.write(BREAK.getBytes());
                requestOutputStream.write("Set-Cookie:".getBytes());
                requestOutputStream.write(sessionValues.toString().getBytes());
                requestOutputStream.write(BREAK.getBytes());
                requestOutputStream.write(("Location: " +  networkRequest.getRedirectLocation() + SPACE).getBytes());
                requestOutputStream.write(BREAK.getBytes());
                requestOutputStream.write("Content-Length: -1".getBytes());
                requestOutputStream.write(DOUBLEBREAK.getBytes());

                requestOutputStream.close();
                socketClient.close();

                networkRequestFactory.execute();
                return;
            }


            requestOutputStream.write("Content-Type:".getBytes());
            requestOutputStream.write(routeResult.getContentType().getBytes());
            requestOutputStream.write(BREAK.getBytes());

            requestOutputStream.write("Set-Cookie:".getBytes());
            requestOutputStream.write(sessionValues.toString().getBytes());
            requestOutputStream.write(DOUBLEBREAK.getBytes());
            requestOutputStream.write(routeResult.getResponseBytes());

            requestOutputStream.close();
            socketClient.close();

            networkRequestFactory.execute();

        }catch(IOException | InterruptedException ex){
            try {
                networkRequestFactory.execute();
                return;
            } catch (IOException | InterruptedException iox) {
                try {
                    networkRequestFactory.execute();
                    return;
                } catch (IOException | InterruptedException ioException) {
                    ioException.printStackTrace();
                }
                iox.printStackTrace();
            }
            ex.printStackTrace();
        }
    }

}
