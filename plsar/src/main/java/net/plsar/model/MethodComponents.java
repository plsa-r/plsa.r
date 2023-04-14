package net.plsar.model;

import net.plsar.MethodAttribute;

import java.util.*;

public class MethodComponents {
    public MethodComponents(){
        this.routeMethodAttributeVariablesList = new ArrayList<>();
        this.routeMethodAttributesList = new ArrayList<>();
        this.routeMethodAttributes = new LinkedHashMap<>();
    }

    List<Object> routeMethodAttributesList;
    List<Object> routeMethodAttributeVariablesList;
    LinkedHashMap<String, MethodAttribute> routeMethodAttributes;

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

    public LinkedHashMap<String, MethodAttribute> getRouteMethodAttributes() {
        return routeMethodAttributes;
    }

    public void setRouteMethodAttributes(LinkedHashMap<String, MethodAttribute> routeMethodAttributes) {
        this.routeMethodAttributes = routeMethodAttributes;
    }
}
