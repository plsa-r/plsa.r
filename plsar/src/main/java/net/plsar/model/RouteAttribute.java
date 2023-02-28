package net.plsar.model;

public class RouteAttribute {

    String qualifiedName;
    String typeKlass;
    Integer routePosition;
    Boolean routeVariable;//todo:necessary?

    public String getQualifiedName() {
        return qualifiedName;
    }

    public void setQualifiedName(String qualifiedName) {
        this.qualifiedName = qualifiedName;
    }

    public String getTypeKlass() {
        return typeKlass;
    }

    public void setTypeKlass(String typeKlass) {
        this.typeKlass = typeKlass;
    }

    public Integer getRoutePosition() {
        return routePosition;
    }

    public void setRoutePosition(Integer routePosition) {
        this.routePosition = routePosition;
    }

    public Boolean getRouteVariable() {
        return routeVariable;
    }

    public void setRouteVariable(Boolean routeVariable) {
        this.routeVariable = routeVariable;
    }
}
