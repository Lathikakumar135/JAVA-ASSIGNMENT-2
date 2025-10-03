package model;

import java.util.*;

public class Student extends Person {
    private Set<Course> registeredCourses;
    private Transcript transcript;

    public Student(String id, String name) {
        super(id, name);
        this.registeredCourses = new LinkedHashSet<>();
        this.transcript = new Transcript(this);
    }

    public Set<Course> getRegisteredCourses() {
        return Collections.unmodifiableSet(registeredCourses);
    }

    public Transcript getTranscript() {
        return transcript;
    }

    public void registerCourse(Course c) {
        if (c != null) registeredCourses.add(c);
    }

    public boolean isRegisteredFor(Course c) {
        return registeredCourses.contains(c);
    }

    @Override
    public void displayInfo() {
        System.out.println("Student: " + getName() + " (" + getId() + ")");
    }

    @Override
    public String toString() {
        return getId() + " - " + getName();
    }
}
