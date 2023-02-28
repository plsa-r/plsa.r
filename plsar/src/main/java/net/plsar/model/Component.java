package net.plsar.model;

public class Component {
    String component;
    Integer activeBeginIndex;
    Integer activeCloseIndex;

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public Integer getActiveBeginIndex() {
        return activeBeginIndex;
    }

    public void setActiveBeginIndex(Integer activeBeginIndex) {
        this.activeBeginIndex = activeBeginIndex;
    }

    public Integer getActiveCloseIndex() {
        return activeCloseIndex;
    }

    public void setActiveCloseIndex(Integer activeCloseIndex) {
        this.activeCloseIndex = activeCloseIndex;
    }

    public Component(String component){
        this.component = component;
    }
}
