package model;

public abstract class Person {
    private String id;
    private String name;

    public Person(String id, String name) {
        this.id = id.trim();
        this.name = name.trim();
    }

    public String getId() { return id; }
    public String getName() { return name; }

    public abstract void displayInfo();
}
