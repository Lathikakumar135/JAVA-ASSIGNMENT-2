package main;

import service.UniversityService;
import util.InputValidator;
import exceptions.InvalidInputException;
import model.Course;
import model.Assessment;

import java.time.LocalDate;
import java.util.Scanner;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        UniversityService svc = new UniversityService();
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n--- University Exams & Grading ---");
            System.out.println("1. Add Course");
            System.out.println("2. Add Student");
            System.out.println("3. Register Student to Course");
            System.out.println("4. Create Assessment");
            System.out.println("5. Schedule Exam");
            System.out.println("6. Record Marks (per assessment)");
            System.out.println("7. Publish Grades for Course");
            System.out.println("8. Generate Transcript (Student)");
            System.out.println("9. Show Grade Sheet (Course)");
            System.out.println("10. Assessment Analytics (Course)");
            System.out.println("11. List Courses / Students");
            System.out.println("12. Show Raw Marks (debug)");
            System.out.println("13. Exit");
            int choice = InputValidator.readInt(sc, "Enter choice: ", 1, 13);

            try {
                switch (choice) {
                    case 1 -> {
                        String cid = InputValidator.readNonEmpty(sc, "Course ID: ");
                        String title = InputValidator.readNonEmpty(sc, "Course title: ");
                        int credits = InputValidator.readInt(sc, "Credits: ", 1, 10);
                        svc.addCourse(cid, title, credits);
                        System.out.println("Course added.");
                    }
                    case 2 -> {
                        String sid = InputValidator.readNonEmpty(sc, "Student ID: ");
                        String name = InputValidator.readNonEmpty(sc, "Student name: ");
                        svc.addStudent(sid, name);
                        System.out.println("Student added.");
                    }
                    case 3 -> {
                        String sid = InputValidator.readNonEmpty(sc, "Student ID: ");
                        String cid = InputValidator.readNonEmpty(sc, "Course ID: ");
                        svc.registerStudentToCourse(sid, cid);
                        System.out.println("Registration successful.");
                    }
                    case 4 -> {
                        String cid = InputValidator.readNonEmpty(sc, "Course ID: ");
                        String aid = InputValidator.readNonEmpty(sc, "Assessment ID: ");
                        String aname = InputValidator.readNonEmpty(sc, "Assessment name: ");
                        double weight = InputValidator.readDouble(sc, "Weightage (0-100): ", 0.1, 100.0);
                        double max = InputValidator.readDouble(sc, "Max marks: ", 1.0, 10000.0);
                        svc.createAssessment(cid, aid, aname, weight, max);
                        System.out.println("Assessment created.");
                    }
                    case 5 -> {
                        String cid = InputValidator.readNonEmpty(sc, "Course ID: ");
                        String dateStr = InputValidator.readNonEmpty(sc, "Exam date (YYYY-MM-DD): ");
                        String venue = InputValidator.readNonEmpty(sc, "Venue: ");
                        LocalDate d = LocalDate.parse(dateStr);
                        svc.scheduleExam(cid, d, venue);
                        System.out.println("Exam scheduled.");
                    }
                    case 6 -> {
                        String sid = InputValidator.readNonEmpty(sc, "Student ID: ");
                        String cid = InputValidator.readNonEmpty(sc, "Course ID: ");
                        Course course = svc.getCourse(cid);
                        if (course == null) { System.out.println("Course not found."); break; }
                        System.out.println("Assessments:");
                        for (Assessment a : course.getAssessments()) {
                            System.out.println(" - " + a.getId() + " : " + a.getName() + " (" + a.getWeightage() + "%, max " + a.getMaxMarks() + ")");
                        }
                        String aid = InputValidator.readNonEmpty(sc, "Assessment ID: ");
                        double marks = InputValidator.readDouble(sc, "Marks obtained: ", 0.0, 100000.0);
                        System.out.println("Is this a resit/improvement? 1=Yes 2=No");
                        int r = InputValidator.readInt(sc, "Choice: ", 1, 2);
                        boolean isResit = (r == 1);
                        try {
                            svc.recordMark(sid, cid, aid, marks, isResit);
                            System.out.println("Marks recorded.");
                        } catch (InvalidInputException ex) {
                            System.out.println("Error recording marks: " + ex.getMessage());
                        }
                    }
                    case 7 -> {
                        String cid = InputValidator.readNonEmpty(sc, "Course ID to publish grades: ");
                        svc.publishGradesForCourse(cid);
                        System.out.println("Grades published and transcripts updated.");
                    }
                    case 8 -> {
                        String sid = InputValidator.readNonEmpty(sc, "Student ID: ");
                        svc.generateTranscriptForStudent(sid);
                    }
                    case 9 -> {
                        String cid = InputValidator.readNonEmpty(sc, "Course ID: ");
                        svc.gradeSheetForCourse(cid);
                    }
                    case 10 -> {
                        String cid = InputValidator.readNonEmpty(sc, "Course ID: ");
                        svc.assessmentAnalytics(cid);
                    }
                    case 11 -> {
                        System.out.println("Courses:");
                        List<Course> courses = svc.listCourses();
                        courses.forEach(c -> System.out.println(" - " + c));
                        System.out.println("Students:");
                        svc.listStudents().forEach(s -> System.out.println(" - " + s));
                    }
                    case 12 -> { svc.printAllMarks(); }
                    case 13 -> {
                        System.out.println("Exiting...");
                        sc.close();
                        return;
                    }
                }
            } catch (InvalidInputException ex) {
                System.out.println("Operation failed: " + ex.getMessage());
            } catch (Exception ex) {
                System.out.println("Unexpected error: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }
}
