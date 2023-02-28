package net.plsar.model;

import java.util.List;

public class IterableResult {
    String field;
    String key;
    List<Object> mojos;//todo: not mojos, pojos, but mojos sound cooler, thank you maven

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<Object> getMojos() {
        return mojos;
    }

    public void setMojos(List<Object> mojos) {
        this.mojos = mojos;
    }
}
