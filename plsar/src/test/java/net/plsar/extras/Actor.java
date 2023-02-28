package net.plsar.extras;

public class Actor {
    Integer id;
    String name;
    Integer age;
    Actor wife;
    Pet pet;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Actor getWife() {
        return wife;
    }

    public void setWife(Actor wife) {
        this.wife = wife;
    }

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }
}
