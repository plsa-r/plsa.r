package net.plsar.model;

import net.plsar.resources.StargzrResources;

public class RouteGuid {
    String guid;

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public RouteGuid(StargzrResources stargzrResources){
        this.guid = stargzrResources.getGuid(24);
    }
}
