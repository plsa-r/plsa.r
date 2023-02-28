package net.plsar;

import net.plsar.model.FlashMessage;
import net.plsar.model.ViewCache;
import net.plsar.security.SecurityAttributes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;

public class NetworkRequestFactory {
    FlashMessage flashMessage;
    ViewCache viewCache;

    ViewConfig viewConfig;

    String renderer;
    String resourcesDirectory;

    RouteAttributes routeAttributes;
    SecurityAttributes securityAttributes;

    ConcurrentMap<String, byte[]> viewBytesMap;
    ServerSocket serverSocket;

    List<Class<?>> viewRenderers;

    InputStream requestInputStream;
    OutputStream requestOutputStream;
    ExecutorService executorService;

    PersistenceConfig persistenceConfig;
    Class<?> securityAccessKlass;

    public void execute() throws IOException, InterruptedException {
        closeStreamConnections();
        executorService.execute(new NetworkRequestExecutor(renderer, resourcesDirectory, routeAttributes, viewConfig, viewBytesMap, executorService, serverSocket, viewRenderers, persistenceConfig, securityAccessKlass, flashMessage, viewCache));
    }

    public ViewConfig getViewConfig() {
        return viewConfig;
    }

    public void setViewConfig(ViewConfig viewConfig) {
        this.viewConfig = viewConfig;
    }
    public ViewCache getViewCache() {
        return viewCache;
    }

    public void setViewCache(ViewCache viewCache) {
        this.viewCache = viewCache;
    }

    public FlashMessage getFlashMessage() {
        return flashMessage;
    }

    public void setFlashMessage(FlashMessage flashMessage) {
        this.flashMessage = flashMessage;
    }

    public String getRenderer() {
        return renderer;
    }

    public void setRenderer(String renderer) {
        this.renderer = renderer;
    }

    public String getResourcesDirectory() {
        return resourcesDirectory;
    }

    public void setResourcesDirectory(String resourcesDirectory) {
        this.resourcesDirectory = resourcesDirectory;
    }

    public SecurityAttributes getSecurityAttributes() {
        return securityAttributes;
    }

    public void setSecurityAttributes(SecurityAttributes securityAttributes) {
        this.securityAttributes = securityAttributes;
    }

    public ConcurrentMap<String, byte[]> getViewBytesMap() {
        return viewBytesMap;
    }

    public void setViewBytesMap(ConcurrentMap<String, byte[]> viewBytesMap) {
        this.viewBytesMap = viewBytesMap;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public List<Class<?>> getViewRenderers() {
        return viewRenderers;
    }

    public void setViewRenderers(List<Class<?>> viewRenderers) {
        this.viewRenderers = viewRenderers;
    }

    public InputStream getRequestInputStream() {
        return requestInputStream;
    }

    public void setRequestInputStream(InputStream requestInputStream) {
        this.requestInputStream = requestInputStream;
    }

    public OutputStream getRequestOutputStream() {
        return requestOutputStream;
    }

    public void setRequestOutputStream(OutputStream requestOutputStream) {
        this.requestOutputStream = requestOutputStream;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public RouteAttributes getRouteAttributes() {
        return routeAttributes;
    }

    public void setRouteAttributes(RouteAttributes routeAttributes) {
        this.routeAttributes = routeAttributes;
    }

    public PersistenceConfig getPersistenceConfig() {
        return persistenceConfig;
    }

    public void setPersistenceConfig(PersistenceConfig persistenceConfig) {
        this.persistenceConfig = persistenceConfig;
    }

    public Class<?> getSecurityAccessKlass() {
        return securityAccessKlass;
    }

    public void setSecurityAccessKlass(Class<?> securityAccessKlass) {
        this.securityAccessKlass = securityAccessKlass;
    }

    public void closeStreamConnections() throws IOException {
        requestInputStream.close();
        requestOutputStream.flush();
        requestOutputStream.close();
    }

}
