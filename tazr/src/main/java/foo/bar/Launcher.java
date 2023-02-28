package foo.bar;

import net.plsar.*;
import net.plsar.drivers.Drivers;
import net.plsar.environments.Environments;
import net.plsar.schemes.RenderingScheme;

public class Launcher {
    public static void main(String[] args){
        PLSAR PLSAR = new PLSAR(9000);
        PLSAR.setNumberOfPartitions(30);
        PLSAR.setNumberOfRequestExecutors(70);

        PersistenceConfig persistenceConfig = new PersistenceConfig();
        persistenceConfig.setDriver(Drivers.H2);
        persistenceConfig.setUrl("jdbc:h2:~/tazr");
        persistenceConfig.setUser("sa");
        persistenceConfig.setPassword("");

        SchemaConfig schemaConfig = new SchemaConfig();
        schemaConfig.setSchema("schema.sql");
        schemaConfig.setEnvironment(Environments.DEVELOPMENT);
        persistenceConfig.setSchemaConfig(schemaConfig);

        ViewConfig viewConfig = new ViewConfig();
        viewConfig.setResourcesPath("assets");
        viewConfig.setRenderingScheme(RenderingScheme.RELOAD_EACH_REQUEST);

        PropertiesConfig propertiesConfig = new PropertiesConfig();
        propertiesConfig.setPropertiesFile("tazr.properties");

        PLSAR.setPersistenceConfig(persistenceConfig);
        PLSAR.setPropertiesConfig(propertiesConfig);
        PLSAR.setSecurityAccess(AuthAccess.class);
        PLSAR.setViewConfig(viewConfig);

        PLSAR.start();
    }
}
