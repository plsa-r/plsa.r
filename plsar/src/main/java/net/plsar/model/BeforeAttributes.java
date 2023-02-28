package net.plsar.model;

import java.util.HashMap;
import java.util.Map;

public class BeforeAttributes {
    public BeforeAttributes() {
        this.attributes = new HashMap<>();
    }

    Map<String, Object> attributes;

    public Object get(String principalKey){
        String principalAttributesKey = principalKey.toLowerCase();
        if(this.attributes.containsKey(principalAttributesKey)){
            return this.attributes.get(principalAttributesKey);
        }
        return null;
    }

    public void set(String key, Object value){
        this.attributes.put(key, value);
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

}
