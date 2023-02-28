package net.plsar;

import net.plsar.implement.ViewRenderer;
import net.plsar.security.SecurityManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class RouteAttributes {

    public RouteAttributes(){
        this.attributes = new ConcurrentHashMap<>();
//        this.sessions = new ConcurrentHashMap<>();
        this.routeEndpointHolder = new RouteEndpointHolder();
        this.sessionRegistry = new ConcurrentHashMap<>();
    }

    PersistenceConfig persistenceConfig;

    Map<String, String> attributes;
//    ConcurrentMap<String, NetworkSession> sessions;
    ConcurrentMap<String, ViewRenderer> viewRenderers;
    ConcurrentMap<String, Boolean> sessionRegistry;
    RouteEndpointHolder routeEndpointHolder;

    Class<?> securityAccess;
    SecurityManager securityManager;

    public String get(String key){
        if(this.attributes.containsKey(key)){
            return this.attributes.get(key);
        }
        return null;
    }

    public void set(String key, String value){
        this.attributes.put(key, value);
    }

    public PersistenceConfig getPersistenceConfig() {
        return persistenceConfig;
    }

    public void setPersistenceConfig(PersistenceConfig persistenceConfig) {
        this.persistenceConfig = persistenceConfig;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

//    public ConcurrentMap<String, NetworkSession> getSessions() {
//        return sessions;
//    }
//
//    public void setSessions(ConcurrentMap<String, NetworkSession> sessions) {
//        this.sessions = sessions;
//    }

    public ConcurrentMap<String, ViewRenderer> getViewRenderers() {
        return viewRenderers;
    }

    public void setViewRenderers(ConcurrentMap<String, ViewRenderer> viewRenderers) {
        this.viewRenderers = viewRenderers;
    }

    public ConcurrentMap<String, Boolean> getSessionRegistry() {
        return sessionRegistry;
    }

    public void setSessionRegistry(ConcurrentMap<String, Boolean> sessionRegistry) {
        this.sessionRegistry = sessionRegistry;
    }

    public RouteEndpointHolder getRouteEndpointHolder() {
        return routeEndpointHolder;
    }

    public void setRouteEndpointHolder(RouteEndpointHolder routeEndpointHolder) {
        this.routeEndpointHolder = routeEndpointHolder;
    }

    public Class<?> getSecurityAccess() {
        return securityAccess;
    }

    public void setSecurityAccess(Class<?> securityAccess) {
        this.securityAccess = securityAccess;
    }

    public SecurityManager getSecurityManager() {
        return securityManager;
    }

    public void setSecurityManager(SecurityManager securityManager) {
        this.securityManager = securityManager;
    }
}
