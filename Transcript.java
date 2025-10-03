package model;

import java.util.*;

public class Transcript {
    private Student student;
    // courseId -> Grade
    private Map<String, Grade> grades = new LinkedHashMap<>();
    private double gpa; // computed by service

    public Transcript(Student student) {
        this.student = student;
    }

    public void addOrUpdateGrade(String courseId, Grade g) {
        grades.put(courseId, g);
    }

    public Map<String, Grade> getGrades() {
        return Collections.unmodifiableMap(grades);
    }

    public void setGpa(double gpa) { this.gpa = gpa; }
    public double getGpa() { return gpa; }

    public void printTranscript(Map<String, Course> courseLookup) {
        System.out.println("Transcript for: " + student.getName() + " (" + student.getId() + ")");
        System.out.printf("%-10s %-30s %-8s %-20s\n", "CourseID", "CourseTitle", "Credits", "Grade");
        for (Map.Entry<String, Grade> e : grades.entrySet()) {
            Course c = courseLookup.get(e.getKey());
            Grade g = e.getValue();
            String title = c == null ? "N/A" : c.getTitle();
            int credits = c == null ? 0 : c.getCredits();
            System.out.printf("%-10s %-30s %-8d %-20s\n", e.getKey(), title, credits, g);
        }
        System.out.println("GPA: " + String.format("%.3f", gpa));
    }
}
