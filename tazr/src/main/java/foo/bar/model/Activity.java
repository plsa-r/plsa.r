package foo.bar.model;

public class Activity {
    Long id;
    String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        if(this.description == null) return "";
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
