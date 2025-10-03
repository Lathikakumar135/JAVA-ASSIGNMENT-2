package model;

import java.util.*;

public class Course {
    private String courseId;
    private String title;
    private int credits;
    private List<Assessment> assessments;
    private ExamSchedule schedule;

    public Course(String courseId, String title, int credits) {
        this.courseId = courseId.trim();
        this.title = title.trim();
        this.credits = credits;
        this.assessments = new ArrayList<>();
    }

    public String getCourseId() { return courseId; }
    public String getTitle() { return title; }
    public int getCredits() { return credits; }

    public List<Assessment> getAssessments() { return Collections.unmodifiableList(assessments); }

    public void addAssessment(Assessment a) {
        if (a != null) assessments.add(a);
    }

    public ExamSchedule getSchedule() { return schedule; }
    public void setSchedule(ExamSchedule schedule) { this.schedule = schedule; }

    @Override
    public String toString() {
        return courseId + " - " + title + " (" + credits + "cr)";
    }
}
