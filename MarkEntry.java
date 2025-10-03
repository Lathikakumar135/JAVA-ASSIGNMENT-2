package model;

import java.time.LocalDateTime;

public class MarkEntry {
    private String studentId;
    private String courseId;
    private String assessmentId;
    private double marksObtained; // raw marks for the assessment
    private boolean resit;
    private LocalDateTime recordedAt;

    public MarkEntry(String studentId, String courseId, String assessmentId, double marksObtained, boolean resit) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.assessmentId = assessmentId;
        this.marksObtained = marksObtained;
        this.resit = resit;
        this.recordedAt = LocalDateTime.now();
    }

    public String getStudentId() { return studentId; }
    public String getCourseId() { return courseId; }
    public String getAssessmentId() { return assessmentId; }
    public double getMarksObtained() { return marksObtained; }
    public boolean isResit() { return resit; }
    public LocalDateTime getRecordedAt() { return recordedAt; }

    public void setMarksObtained(double marks) {
        this.marksObtained = marks;
        this.recordedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return studentId + "|" + courseId + "|" + assessmentId + " => " + marksObtained + (resit ? " (resit)" : "");
    }
}
