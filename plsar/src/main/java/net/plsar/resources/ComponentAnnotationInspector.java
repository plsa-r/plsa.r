package net.plsar.resources;

import net.plsar.annotations.Repository;
import net.plsar.annotations.Service;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ComponentAnnotationInspector {

    ClassLoader klassLoader;
    ComponentsHolder componentsHolder;

    public ComponentAnnotationInspector(ComponentsHolder componentsHolder){
        klassLoader = Thread.currentThread().getContextClassLoader();
        this.componentsHolder = componentsHolder;
    }

    public void inspect(){
        Path filePath = Paths.get("build");//requires build directory
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

                if(klass.isAnnotationPresent(Repository.class)){
                    AnnotationComponent annotationComponent = new AnnotationComponent();
                    annotationComponent.setKlass(klass);
                    String[] componentElements = klass.getName().split("\\.");
                    String componentKey = componentElements[componentElements.length -1].toLowerCase();
                    componentsHolder.getRepositories().put(componentKey, klass);
                }
                if(klass.isAnnotationPresent(Service.class)){
                    AnnotationComponent annotationComponent = new AnnotationComponent();
                    annotationComponent.setKlass(klass);
                    String[] componentElements = klass.getName().split("\\.");
                    String componentKey = componentElements[componentElements.length -1].toLowerCase();
                    componentsHolder.getServices().put(componentKey, klass);
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
