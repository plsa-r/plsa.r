package net.plsar.resources;

import net.plsar.annotations.RouteRegistration;
import net.plsar.annotations.ServerStartup;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

public class StartupAnnotationInspector {

    Logger Log = Logger.getLogger(StartupAnnotationInspector.class.getName());

    ClassLoader klassLoader;
    ComponentsHolder componentsHolder;

    public StartupAnnotationInspector(ComponentsHolder componentsHolder){
        klassLoader = Thread.currentThread().getContextClassLoader();
        this.componentsHolder = componentsHolder;
    }

    public void inspect(){
        Path filePath = Paths.get("build");//requires "build" directory
        String completeFilePath = filePath.toAbsolutePath().toString();
        inspectFilePath(completeFilePath);
    }

    public void inspectFilePath(String filePath){
        File pathFile = new File(filePath);

        File[] files = pathFile.listFiles();
        for (File file : files) {

            if (file.isDirectory()) {
                inspectFilePath(file.getPath());
                continue;
            }

            try {

                if(!file.getPath().endsWith(".class"))continue;

                String separator = System.getProperty("file.separator");
                String regex = "classes" + "\\" + separator;//todo:fix
                String[] klassPathParts = file.getPath().split(regex);
                String klassPathSlashesRemoved =  klassPathParts[1].replace("\\", ".");
                String klassPathPeriod = klassPathSlashesRemoved.replace("/",".");
                String klassPathBefore = klassPathPeriod.replace("."+ "class", "");

                String klassPath = klassPathBefore.replaceFirst("java.", "").replaceFirst("main.", "");

                Class<?> klass = klassLoader.loadClass(klassPath);

                if (klass.isAnnotation() || klass.isInterface()) continue;

                if(klass.isAnnotationPresent(ServerStartup.class)) {
                    AnnotationComponent annotationComponent = new AnnotationComponent();
                    annotationComponent.setKlass(klass);
                    componentsHolder.setServerStartup(annotationComponent);
                }

            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }

    public ComponentsHolder getComponentsHolder() {
        return this.componentsHolder;
    }
}
