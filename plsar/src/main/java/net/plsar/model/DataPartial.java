package net.plsar.model;

import java.util.*;

public class DataPartial {
    Integer idx;
    String guid;
    String entry;
    String field;
    boolean spec;
    boolean iterable;
    boolean specRequired;
    boolean withinSpec;
    boolean withinIterable;
    boolean endIterable;
    boolean endSpec;
    boolean setVar;
    List<Object> mojos;
    List<ObjectComponent> components;
    List<DataPartial> specPartials;
    Map<String, DataPartial> specPartialsMap;//todo:remove specpartials list rename

    public Integer getIdx() {
        return idx;
    }

    public void setIdx(Integer idx) {
        this.idx = idx;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getEntry() {
        return entry;
    }

    public void setEntry(String entry) {
        this.entry = entry;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public boolean isSpec() {
        return spec;
    }

    public void setSpec(boolean spec) {
        this.spec = spec;
    }

    public boolean isIterable() {
        return iterable;
    }

    public void setIterable(boolean iterable) {
        this.iterable = iterable;
    }

    public boolean getSpecRequired() {
        return specRequired;
    }

    public void setSpecRequired(boolean specRequired) {
        this.specRequired = specRequired;
    }

    public boolean isWithinSpec() {
        return withinSpec;
    }

    public void setWithinSpec(boolean withinSpec) {
        this.withinSpec = withinSpec;
    }

    public boolean isWithinIterable() {
        return withinIterable;
    }

    public void setWithinIterable(boolean withinIterable) {
        this.withinIterable = withinIterable;
    }

    public boolean isEndIterable() {
        return endIterable;
    }

    public void setEndIterable(boolean endIterable) {
        this.endIterable = endIterable;
    }

    public boolean isEndSpec() {
        return endSpec;
    }

    public void setEndSpec(boolean endSpec) {
        this.endSpec = endSpec;
    }

    public boolean isSetVar() {
        return setVar;
    }

    public void setSetVar(boolean setVar) {
        this.setVar = setVar;
    }

    public boolean hasSpecs() {
        return !specPartials.isEmpty();
    }

    public List<ObjectComponent> getComponents() {
        return components;
    }

    public void setComponents(List<ObjectComponent> components) {
        this.components = components;
    }

    public List<DataPartial> getSpecPartials() {
        return specPartials;
    }

    public void setSpecPartials(List<DataPartial> specPartials) {
        this.specPartials = specPartials;
    }

    public String toString(){
        return this.entry;
    }

    public DataPartial(String entry){
        this.entry = entry;
    }

    public DataPartial(){
        this.guid = UUID.randomUUID().toString();
        this.field = "";
        this.mojos = new ArrayList<>();
        this.components = new ArrayList<>();
        this.specPartials = new ArrayList<>();
        this.specPartialsMap = new LinkedHashMap();
    }

}
