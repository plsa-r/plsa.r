package net.plsar;

import java.util.HashMap;
import java.util.Map;

public class RouteEndpointHolder {
    Map<String, RouteEndpoint> routeEndpoints;

    public Map<String, RouteEndpoint> getRouteEndpoints() {
        return routeEndpoints;
    }

    public void setRouteEndpoints(Map<String, RouteEndpoint> routeEndpoints) {
        this.routeEndpoints = routeEndpoints;
    }

    public RouteEndpointHolder(){
        this.routeEndpoints = new HashMap<>();
    }
}
