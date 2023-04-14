package net.plsar;

import net.plsar.annotations.Before;
import net.plsar.implement.RouteEndpointBefore;
import net.plsar.model.*;
import net.plsar.security.SecurityManager;
import net.plsar.resources.StargzrResources;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class BeforeRouteResolver {


    FlashMessage flashMessage;
    ViewCache viewCache;
    Object routeInstance;
    NetworkRequest networkRequest;
    NetworkResponse networkResponse;
    SecurityManager securityManager;
    MethodComponents methodComponents;
    Method routeEndpointInstanceMethod;
    Map<String, Object> routeEndpointInstances;

    public BeforeResult resolve() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, PlsarException {

        BeforeResult beforeResult = null;
        StargzrResources stargzrResources = new StargzrResources();

        if(routeEndpointInstanceMethod.isAnnotationPresent(Before.class)){
            Before beforeAnnotation = routeEndpointInstanceMethod.getAnnotation(Before.class);

            String routePrincipalVariablesElement = beforeAnnotation.variables();
            BeforeAttributes beforeAttributes = new BeforeAttributes();

            if(!methodComponents.getRouteMethodAttributeVariablesList().isEmpty()) {
                Integer routeVariableIndex = 0;
                String[] routePrincipalVariables = routePrincipalVariablesElement.split(",");
                List<Object> routeAttributesVariableList = methodComponents.getRouteMethodAttributeVariablesList();
                for (String routePrincipalVariableElement : routePrincipalVariables) {
                    Object routePrincipalVariableValue = routeAttributesVariableList.get(routeVariableIndex);
                    String routePrincipalVariable = routePrincipalVariableElement.replace("{", "")
                            .replace("}", "").trim();
                    beforeAttributes.set(routePrincipalVariable, routePrincipalVariableValue);
                }
            }

            for(Map.Entry<String, Object> routePrincipalInstance : routeEndpointInstances.entrySet()){
                String routePrincipalInstanceKey = routePrincipalInstance.getKey().toLowerCase();
                beforeAttributes.set(routePrincipalInstanceKey, routePrincipalInstance.getValue());
            }

            Class<?>[] routePrincipalKlasses = beforeAnnotation.value();
            if(routePrincipalKlasses.length > 0){
                for(Class<?> routePrincipalKlass : routePrincipalKlasses) {
                    RouteEndpointBefore routePrincipal = (RouteEndpointBefore) routePrincipalKlass.getConstructor().newInstance();
                    beforeResult = routePrincipal.before(flashMessage, viewCache, networkRequest, networkResponse, securityManager, beforeAttributes);
                    if(beforeResult != null) {
                        if (!beforeResult.getRedirectUri().equals("")) {
                            RedirectInfo redirectInfo = new RedirectInfo();
                            redirectInfo.setMethodName(routeEndpointInstanceMethod.getName());
                            redirectInfo.setKlassName(routeInstance.getClass().getName());

                            if (beforeResult.getRedirectUri() == null || beforeResult.getRedirectUri().equals("")) {
                                throw new PlsarException("redirect uri is empty on " + routePrincipalKlass.getName());
                            }

                            String redirectRouteUri = stargzrResources.getRedirect(beforeResult.getRedirectUri());

                            if (!beforeResult.getMessage().equals("")) {
                                viewCache.set("message", beforeResult.getMessage());
                            }

                            networkRequest.setRedirect(true);
                            networkRequest.setRedirectLocation(redirectRouteUri);
                            break;
                        }
                    }
                }
            }
        }
        return beforeResult;
    }


    public void setViewCache(ViewCache viewCache) {
        this.viewCache = viewCache;
    }

    public void setRouteInstance(Object routeInstance) {
        this.routeInstance = routeInstance;
    }

    public void setNetworkRequest(NetworkRequest networkRequest) {
        this.networkRequest = networkRequest;
    }

    public void setNetworkResponse(NetworkResponse networkResponse) {
        this.networkResponse = networkResponse;
    }

    public void setSecurityManager(SecurityManager securityManager) {
        this.securityManager = securityManager;
    }

    public void setMethodComponents(MethodComponents methodComponents) {
        this.methodComponents = methodComponents;
    }

    public void setRouteEndpointInstanceMethod(Method routeEndpointInstanceMethod) {
        this.routeEndpointInstanceMethod = routeEndpointInstanceMethod;
    }

    public void setRouteEndpointInstances(Map<String, Object> routeEndpointInstances) {
        this.routeEndpointInstances = routeEndpointInstances;
    }

}
