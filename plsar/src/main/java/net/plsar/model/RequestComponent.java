package net.plsar.model;

import java.util.ArrayList;
import java.util.List;

public class RequestComponent {
    String name;
    String value;
    boolean hasFiles;

    List<FileComponent> fileComponents;
    List<String> values;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isHasFiles() {
        return hasFiles;
    }

    public void setHasFiles(boolean hasFiles) {
        this.hasFiles = hasFiles;
    }

    public List<FileComponent> getFileComponents() {
        return fileComponents;
    }

    public void setFileComponents(List<FileComponent> fileComponents) {
        this.fileComponents = fileComponents;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    public RequestComponent(){
        this.values = new ArrayList<>();
        this.fileComponents = new ArrayList<>();
    }
}
