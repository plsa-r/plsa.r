package net.plsar.model;

public class ObjectComponent {
    String activeField;
    Object object;

    public String getActiveField() {
        return activeField;
    }

    public void setActiveField(String activeField) {
        this.activeField = activeField;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public String toString(){
        return this.activeField + ":" + this.object;
    }
}
