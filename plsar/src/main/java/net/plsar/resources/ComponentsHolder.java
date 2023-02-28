package net.plsar.resources;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ComponentsHolder {
    AnnotationComponent serverStartup;
    AnnotationComponent routeRegistration;
    ConcurrentMap<String, Class<?>> repositories;
    ConcurrentMap<String, Class<?>> services;

    public ComponentsHolder(){
        this.services = new ConcurrentHashMap<>();
        this.repositories = new ConcurrentHashMap<>();
    }

    public ConcurrentMap<String, Class<?>> getRepositories() {
        return repositories;
    }

    public void setRepositories(ConcurrentMap<String, Class<?>> repositories) {
        this.repositories = repositories;
    }

    public ConcurrentMap<String, Class<?>> getServices() {
        return services;
    }

    public void setServices(ConcurrentMap<String, Class<?>> services) {
        this.services = services;
    }

    public AnnotationComponent getServerStartup() {
        return serverStartup;
    }

    public void setServerStartup(AnnotationComponent serverStartup) {
        this.serverStartup = serverStartup;
    }

    public AnnotationComponent getRouteRegistration() {
        return routeRegistration;
    }

    public void setRouteRegistration(AnnotationComponent routeRegistration) {
        this.routeRegistration = routeRegistration;
    }
}
