package net.plsar.model;

public class LineComponent {

    boolean iterated;
    String activeField;
    String objectField;
    String lineElement;

    public boolean isIterated() {
        return iterated;
    }

    public void setIterated(boolean iterated) {
        this.iterated = iterated;
    }

    public String getActiveField() {
        return activeField;
    }

    public void setActiveField(String activeField) {
        this.activeField = activeField;
    }

    public String getObjectField() {
        return objectField;
    }

    public void setObjectField(String objectField) {
        this.objectField = objectField;
    }

    public String getLineElement() {
        return lineElement;
    }

    public void setLineElement(String lineElement) {
        this.lineElement = lineElement;
    }

    public String getCompleteLineFunction(){
        return OPEN + this.lineElement + END_FUNCTION;
    }

    public String getCompleteLineElement(){
        return OPEN + this.lineElement.replaceAll("\\.", "\\.") + END;
    }

    static final String OPEN = "\\$\\{";
    static final String END = "\\}";
    static final String END_FUNCTION = "\\(\\a-zA-Z+)}";
}
