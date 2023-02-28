package net.plsar.model;

import net.plsar.MethodAttribute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MethodComponents {
    public MethodComponents(){
        this.routeMethodAttributeVariablesList = new ArrayList<>();
        this.routeMethodAttributesList = new ArrayList<>();
        this.routeMethodAttributes = new HashMap<>();
    }

    List<Object> routeMethodAttributesList;
    List<Object> routeMethodAttributeVariablesList;
    Map<String, MethodAttribute> routeMethodAttributes;

    public List<Object> getRouteMethodAttributesList() {
        return routeMethodAttributesList;
    }

    public void setRouteMethodAttributesList(List<Object> routeMethodAttributesList) {
        this.routeMethodAttributesList = routeMethodAttributesList;
    }

    public List<Object> getRouteMethodAttributeVariablesList() {
        return routeMethodAttributeVariablesList;
    }

    public void setRouteMethodAttributeVariablesList(List<Object> routeMethodAttributeVariablesList) {
        this.routeMethodAttributeVariablesList = routeMethodAttributeVariablesList;
    }

    public Map<String, MethodAttribute> getRouteMethodAttributes() {
        return routeMethodAttributes;
    }

    public void setRouteMethodAttributes(Map<String, MethodAttribute> routeMethodAttributes) {
        this.routeMethodAttributes = routeMethodAttributes;
    }
}
