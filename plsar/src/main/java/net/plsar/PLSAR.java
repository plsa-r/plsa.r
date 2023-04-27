package net.plsar;

import net.plsar.environments.Environments;
import net.plsar.resources.AnnotationComponent;
import net.plsar.resources.ComponentsHolder;
import net.plsar.resources.StargzrResources;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class PLSAR {

    static Logger Log = Logger.getLogger(PLSAR.class.getName());

    Integer port;
    String PROPERTIES;

    ViewConfig viewConfig;
    PropertiesConfig propertiesConfig;
    Integer numberOfPartitions = 3;
    Integer numberOfRequestExecutors = 7;
    PersistenceConfig persistenceConfig;
    Class<?> securityAccessKlass;
    List<Class<?>> viewRenderers;

    ServerSocket serverSocket;

    public PLSAR(){
        this.port = 9000;
        this.PROPERTIES = "system.properties";
        this.viewConfig = new ViewConfig();
        this.viewRenderers = new ArrayList<>();
    }

    public PLSAR(int port){
        this.port = port;
        this.PROPERTIES = "system.properties";
        this.viewConfig = new ViewConfig();
        this.viewRenderers = new ArrayList<>();
    }

    public void start(){
        try {

            if (persistenceConfig != null &&
                    persistenceConfig.getSchemaConfig() != null &&
                    persistenceConfig.getSchemaConfig().getEnvironment().equals(Environments.DEVELOPMENT)) {
                DatabaseEnvironmentManager databaseEnvironmentManager = new DatabaseEnvironmentManager();
                databaseEnvironmentManager.setPersistenceConfig(persistenceConfig);
                databaseEnvironmentManager.configure();
            }

            StartupAnnotationResolver startupAnnotationResolver = new StartupAnnotationResolver(new ComponentsHolder());
            startupAnnotationResolver.resolve();
            ComponentsHolder componentsHolder = startupAnnotationResolver.getComponentsHolder();

            if (propertiesConfig == null) {
                propertiesConfig = new PropertiesConfig();
                propertiesConfig.setPropertiesFile(PROPERTIES);
            }

            String propertiesFile = propertiesConfig.getPropertiesFile();
            RouteAttributesResolver routeAttributesResolver = new RouteAttributesResolver(propertiesFile);
            RouteAttributes routeAttributes = routeAttributesResolver.resolve();
            AnnotationComponent serverStartup = componentsHolder.getServerStartup();

            StargzrResources stargzrResources = new StargzrResources();

            String resourcesDirectory = viewConfig.getResourcesPath();
            ConcurrentMap<String, byte[]> viewBytesMap = stargzrResources.getViewBytesMap(viewConfig);

            if (serverStartup != null) {
                Method startupMethod = serverStartup.getKlass().getMethod("startup");
                Object startupObject = serverStartup.getKlass().getConstructor().newInstance();
                startupMethod.invoke(startupObject);
            }

            serverSocket = new ServerSocket(port);
            serverSocket.setPerformancePreferences(0, 1, 2);
            ExecutorService executors = Executors.newFixedThreadPool(numberOfPartitions);
            executors.execute(new PartitionExecutor(viewConfig.getRenderingScheme(), numberOfRequestExecutors, resourcesDirectory, routeAttributes, viewConfig, viewBytesMap, serverSocket, persistenceConfig, viewRenderers, securityAccessKlass));

            System.out.println("\n    ____  __   _____ ___      ____");
            System.out.println("   / __ \\/ /  / ___//   |    / __ \\");
            System.out.println("  / /_/ / /   \\__ \\/ /| |   / /_/ /");
            System.out.println(" / ____/ /______/ / ___ |_ / _, _/");
            System.out.println("/_/   /_____/____/_/  |_(_)_/ |_|\n\n");

            System.out.println("INFO: http://localhost:" + port + "/\n\n");

        }catch(IOException | NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException | PlsarException ex){
            ex.printStackTrace();
        }
    }

    public void stop(){
        try {
            serverSocket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void setPropertiesConfig(PropertiesConfig propertiesConfig){
        this.propertiesConfig = propertiesConfig;
    }

    public void setViewConfig(ViewConfig viewConfig) {
        this.viewConfig = viewConfig;
    }

    public void setSecurityAccess(Class<?> securityAccessKlass) {
        this.securityAccessKlass = securityAccessKlass;
    }

    public void setNumberOfPartitions(int numberOfPartitions){
        this.numberOfPartitions = numberOfPartitions;
    }

    public void setNumberOfRequestExecutors(int numberOfRequestExecutors){
        this.numberOfRequestExecutors = numberOfRequestExecutors;
    }

    public PLSAR addViewRenderer(Class<?> viewRenderer){
        this.viewRenderers.add(viewRenderer);
        return this;
    }

    public PLSAR setPersistenceConfig(PersistenceConfig persistenceConfig) {
        this.persistenceConfig = persistenceConfig;
        return this;
    }

}