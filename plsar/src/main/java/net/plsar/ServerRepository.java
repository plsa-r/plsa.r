package net.plsar;

import java.util.HashMap;
import java.util.Map;

public class ServerRepository {
    Map<String, Object> repository;

    public Object get(String key){
        if(repository.containsKey(key)){
            return repository.get(key);
        }
        return null;
    }

    public Map<String, Object> getRepository() {
        return repository;
    }

    public void setRepository(Map<String, Object> repository) {
        this.repository = repository;
    }

    public ServerRepository(){
        this.repository = new HashMap<>();
    }
}
