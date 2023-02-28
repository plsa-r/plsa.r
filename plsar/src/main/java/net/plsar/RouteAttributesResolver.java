package net.plsar;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.Properties;

public class RouteAttributesResolver {

    String propertiesFile;

    public RouteAttributesResolver(String propertiesFile){
        this.propertiesFile = propertiesFile;
    }

    public RouteAttributes resolve(){
        RouteAttributes routeAttributes = new RouteAttributes();

        Path filePath = Paths.get("src", "main", "resources", propertiesFile);
        String propertiesPath = filePath.toAbsolutePath().toString();
        File file = new File(propertiesPath);

        if(!file.exists())return routeAttributes;

        try {

            InputStream is = new FileInputStream(file);

            if(is == null)return null;

            Properties prop = new Properties();
            prop.load(is);

            Enumeration properties = prop.propertyNames();
            while (properties.hasMoreElements()) {
                String key = (String) properties.nextElement();
                String value = prop.getProperty(key);
                routeAttributes.getAttributes().put(key, value);
            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return routeAttributes;
    }
}
