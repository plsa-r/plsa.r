package net.plsar;

import net.plsar.model.RedirectInfo;
import net.plsar.resources.StargzrResources;

import java.net.ServerSocket;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PartitionExecutor implements Runnable {

    String PARTITION_GUID;
    String RENDERER;
    String resourcesDirectory;
    Integer numberOfExecutors;

    ViewConfig viewConfig;

    ServerSocket serverSocket;
    Queue<RouteEndpointNegotiator> routeNegotiators;
    List<Class<?>> viewRenderers;
    ConcurrentMap<String, String> sessionRouteRegistry;
    ConcurrentMap<String, byte[]> viewBytesMap;
    ConcurrentMap<String, RedirectInfo> initialsRegistry;

    RouteAttributes routeAttributes;

    PersistenceConfig persistenceConfig;
    Class<?> securityAccessKlass;

    public PartitionExecutor(String RENDERER, Integer numberOfExecutors, String resourcesDirectory, RouteAttributes routeAttributes, ViewConfig viewConfig, ConcurrentMap<String, byte[]> viewBytesMap, ServerSocket serverSocket, PersistenceConfig persistenceConfig, List<Class<?>> viewRenderers, Class<?> securityAccessKlass) {
        StargzrResources stargzrResources = new StargzrResources();
        String partitionGuid = stargzrResources.getGuid(48);
        this.PARTITION_GUID = partitionGuid;
        this.RENDERER = RENDERER;
        this.securityAccessKlass = securityAccessKlass;
        this.viewConfig = viewConfig;
        this.viewBytesMap = viewBytesMap;
        this.numberOfExecutors = numberOfExecutors;
        this.serverSocket = serverSocket;
        this.viewRenderers = viewRenderers;
        this.resourcesDirectory = resourcesDirectory;
        this.routeAttributes = routeAttributes;
        this.persistenceConfig = persistenceConfig;
        this.sessionRouteRegistry = new ConcurrentHashMap<>();
        this.initialsRegistry = new ConcurrentHashMap<>();
    }

    @Override
    public void run() {
        try {
            ExecutorService executors = Executors.newFixedThreadPool(numberOfExecutors);
            executors.execute(new NetworkRequestExecutor(RENDERER, resourcesDirectory, routeAttributes, viewConfig, viewBytesMap, executors, serverSocket, viewRenderers, persistenceConfig, securityAccessKlass, null, null));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
