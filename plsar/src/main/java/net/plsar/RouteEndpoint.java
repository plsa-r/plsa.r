package net.plsar;

import net.plsar.model.RouteAttribute;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class RouteEndpoint {
    public RouteEndpoint(){
        this.routeAttributes = new HashMap<>();
    }

    String routePath;
    String regexRoutePath;
    String routeVerb;

    Method routeMethod;

    Class<?> klass;
    Boolean regex;

    Map<String, RouteAttribute> routeAttributes;


    public String getRoutePath() {
        return routePath;
    }

    public void setRoutePath(String routePath) {
        this.routePath = routePath;
    }

    public String getRegexRoutePath() {
        return regexRoutePath;
    }

    public void setRegexRoutePath(String regexRoutePath) {
        this.regexRoutePath = regexRoutePath;
    }

    public String getRouteVerb() {
        return routeVerb;
    }

    public void setRouteVerb(String routeVerb) {
        this.routeVerb = routeVerb;
    }

    public Method getRouteMethod() {
        return routeMethod;
    }

    public void setRouteMethod(Method routeMethod) {
        this.routeMethod = routeMethod;
    }

    public Class<?> getKlass() {
        return klass;
    }

    public void setKlass(Class<?> klass) {
        this.klass = klass;
    }

    public Boolean isRegex() {
        return regex;
    }

    public void setRegex(Boolean regex) {
        this.regex = regex;
    }

    public Map<String, RouteAttribute> getRouteAttributes() {
        return routeAttributes;
    }

    public void setRouteAttributes(Map<String, RouteAttribute> routeAttributes) {
        this.routeAttributes = routeAttributes;
    }

    public Boolean getRegex() {
        return regex;
    }
}
