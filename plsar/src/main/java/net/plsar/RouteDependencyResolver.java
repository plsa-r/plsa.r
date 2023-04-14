package net.plsar;

import net.plsar.annotations.Bind;
import net.plsar.resources.ComponentsHolder;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class RouteDependencyResolver {

    Object routeInstance;
    ComponentsHolder componentsHolder;
    RouteAttributes routeAttributes;
    Map<String, Object> routeEndpointInstances;

    public Map<String, Object> resolve() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        routeEndpointInstances = new HashMap<>();
        PersistenceConfig persistenceConfig = routeAttributes.getPersistenceConfig();
        if (persistenceConfig != null) {
            Dao routeDao = new Dao(persistenceConfig);

            Field[] routeFields = routeInstance.getClass().getDeclaredFields();
            for (Field routeField : routeFields) {
                if (routeField.isAnnotationPresent(Bind.class)) {
                    String fieldKey = routeField.getName().toLowerCase();

                    if (componentsHolder.getServices().containsKey(fieldKey)) {
                        Class<?> serviceKlass = componentsHolder.getServices().get(fieldKey);
                        Constructor<?> serviceKlassConstructor = serviceKlass.getConstructor();
                        Object serviceInstance = serviceKlassConstructor.newInstance();

                        Field[] repoFields = serviceInstance.getClass().getDeclaredFields();
                        for (Field repoField : repoFields) {
                            if (repoField.isAnnotationPresent(Bind.class)) {
                                String repoFieldKey = repoField.getName().toLowerCase();

                                if (componentsHolder.getRepositories().containsKey(repoFieldKey)) {
                                    Class<?> repositoryKlass = componentsHolder.getRepositories().get(repoFieldKey);
                                    Constructor<?> repositoryKlassConstructor = repositoryKlass.getConstructor(Dao.class);
                                    Object repositoryInstance = repositoryKlassConstructor.newInstance(routeDao);
                                    repoField.setAccessible(true);
                                    repoField.set(serviceInstance, repositoryInstance);
                                    routeEndpointInstances.put(repoFieldKey, repositoryInstance);
                                }
                            }
                        }

                        routeField.setAccessible(true);
                        routeField.set(routeInstance, serviceInstance);
                    }

                    if (componentsHolder.getRepositories().containsKey(fieldKey)) {
                        Class<?> componentKlass = componentsHolder.getRepositories().get(fieldKey);
                        Constructor<?> componentKlassConstructor = componentKlass.getConstructor(Dao.class);
                        Object componentInstance = componentKlassConstructor.newInstance(routeDao);
                        routeField.setAccessible(true);
                        routeField.set(routeInstance, componentInstance);
                        routeEndpointInstances.put(fieldKey, componentInstance);
                    }
                }
            }

            try {
                Method setPersistenceMethod = routeInstance.getClass().getMethod("setDao", Dao.class);
                setPersistenceMethod.invoke(routeInstance, new Dao(persistenceConfig));
            } catch (NoSuchMethodException nsme) {
            }
        }

        return routeEndpointInstances;
    }

    public void setRouteInstance(Object routeInstance) {
        this.routeInstance = routeInstance;
    }

    public void setComponentsHolder(ComponentsHolder componentsHolder) {
        this.componentsHolder = componentsHolder;
    }

    public void setRouteAttributes(RouteAttributes routeAttributes) {
        this.routeAttributes = routeAttributes;
    }

}
