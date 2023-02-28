package net.plsar.security;

import net.plsar.Dao;
import net.plsar.PersistenceConfig;
import net.plsar.RouteAttributes;
import net.plsar.model.NetworkRequest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SecurityManagerHelper {

    public SecurityManager getSecurityManager(NetworkRequest networkRequest, SecurityAttributes securityAttributes){
        SecurityManager security = null;
        try {
            RouteAttributes routeAttributes = networkRequest.getRouteAttributes();
            PersistenceConfig persistenceConfig = routeAttributes.getPersistenceConfig();
            Dao dao = new Dao(persistenceConfig);
            Class<?> securityAccessClass = routeAttributes.getSecurityAccess();
            SecurityAccess securityAccessInstance = (SecurityAccess) securityAccessClass.getConstructor().newInstance();
            Method setPersistence = securityAccessInstance.getClass().getMethod("setDao", Dao.class);
            setPersistence.invoke(securityAccessInstance, dao);
            security = new SecurityManager(securityAccessInstance, securityAttributes);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return security;
    }
}
