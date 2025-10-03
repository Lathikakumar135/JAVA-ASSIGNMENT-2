package service;

import model.*;
import exceptions.InvalidInputException;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * UniversityService implements:
 * - add/list courses & students
 * - register students to courses
 * - create assessments (ensuring total weight <=100)
 * - schedule exams
 * - record marks per assessment (resit eligibility)
 * - compute total percent per course (weighted)
 * - derive grade from policy
 * - publish grades and compute GPA (weighted by credits)
 * - analytics per assessment & grade sheets
 */
public class UniversityService {
    private Map<String, Course> courses = new LinkedHashMap<>();
    private Map<String, Student> students = new LinkedHashMap<>();
    // key "sid|cid|aid" -> MarkEntry (stores best/last according to policy)
    private Map<String, MarkEntry> marks = new LinkedHashMap<>();

    // ---- Course & Student management ----
    public void addCourse(String cid, String title, int credits) throws InvalidInputException {
        if (cid == null || title == null || cid.isBlank() || title.isBlank()) throw new InvalidInputException("Invalid course data");
        if (credits <= 0) throw new InvalidInputException("Credits must be > 0");
        if (courses.containsKey(cid)) throw new InvalidInputException("Course exists");
        courses.put(cid, new Course(cid, title, credits));
    }

    public void addStudent(String sid, String name) throws InvalidInputException {
        if (sid == null || name == null || sid.isBlank() || name.isBlank()) throw new InvalidInputException("Invalid student data");
        if (students.containsKey(sid)) throw new InvalidInputException("Student exists");
        students.put(sid, new Student(sid, name));
    }

    public void registerStudentToCourse(String sid, String cid) throws InvalidInputException {
        Student s = students.get(sid);
        Course c = courses.get(cid);
        if (s == null) throw new InvalidInputException("Student not found");
        if (c == null) throw new InvalidInputException("Course not found");
        s.registerCourse(c);
    }

    // ---- Assessment management ----
    public void createAssessment(String cid, String aid, String name, double weightage, double maxMarks) throws InvalidInputException {
        Course c = courses.get(cid);
        if (c == null) throw new InvalidInputException("Course not found");
        if (weightage <= 0 || weightage > 100) throw new InvalidInputException("Invalid weightage");
        if (maxMarks <= 0) throw new InvalidInputException("Max marks must be > 0");
        double existing = c.getAssessments().stream().mapToDouble(Assessment::getWeightage).sum();
        if (existing + weightage > 100 + 1e-9) throw new InvalidInputException("Total weightages exceed 100%");
        c.addAssessment(new Assessment(aid, name, weightage, maxMarks));
    }

    // ---- Scheduling ----
    public void scheduleExam(String cid, LocalDate date, String venue) throws InvalidInputException {
        Course c = courses.get(cid);
        if (c == null) throw new InvalidInputException("Course not found");
        c.setSchedule(new ExamSchedule(date, venue));
    }

    // ---- Recording marks per assessment ----
    // Resit rule: allowed if previous mark < resitThreshold (e.g., 50) OR no previous mark.
    private static final double RESIT_THRESHOLD = 50.0;

    private String mkKey(String sid, String cid, String aid) { return sid + "|" + cid + "|" + aid; }

    public void recordMark(String sid, String cid, String aid, double marksObtained, boolean isResit) throws InvalidInputException {
        Student s = students.get(sid);
        Course c = courses.get(cid);
        if (s == null) throw new InvalidInputException("Student not found");
        if (c == null) throw new InvalidInputException("Course not found");
        if (!s.isRegisteredFor(c)) throw new InvalidInputException("Student not registered for course");
        Assessment a = c.getAssessments().stream().filter(x -> x.getId().equals(aid)).findFirst().orElse(null);
        if (a == null) throw new InvalidInputException("Assessment not found");
        if (marksObtained < 0 || marksObtained > a.getMaxMarks()) throw new InvalidInputException("Marks out of range");

        String key = mkKey(sid, cid, aid);
        MarkEntry existing = marks.get(key);
        if (existing == null) {
            // first attempt -> accept
            marks.put(key, new MarkEntry(sid, cid, aid, marksObtained, isResit));
            return;
        } else {
            // if not a resit and existing entry is resit or not -> teacher correction allowed (non-resit input overwrites)
            if (!isResit) {
                existing.setMarksObtained(marksObtained);
                return;
            } else {
                // is resit: allowed only if eligible
                double prevPercent = (existing.getMarksObtained()/a.getMaxMarks())*100.0;
                if (prevPercent >= RESIT_THRESHOLD) {
                    throw new InvalidInputException("Not eligible for resit (previous mark >= " + RESIT_THRESHOLD + "%).");
                }
                // accept only if improvement
                if (marksObtained > existing.getMarksObtained()) {
                    existing.setMarksObtained(marksObtained);
                    // mark as resit
                    marks.put(key, new MarkEntry(sid, cid, aid, marksObtained, true));
                } else {
                    throw new InvalidInputException("Resit did not improve marks; previous marks retained.");
                }
            }
        }
    }

    // ---- Aggregation: compute total percent for student in course using weightages ----
    public double computeTotalPercent(String sid, String cid) {
        Course c = courses.get(cid);
        Student s = students.get(sid);
        if (c == null || s == null) return 0.0;
        double totalPercent = 0.0; // out of 100
        for (Assessment a : c.getAssessments()) {
            String key = mkKey(sid, cid, a.getId());
            MarkEntry me = marks.get(key);
            double obtained = (me == null) ? 0.0 : me.getMarksObtained();
            double percentOfAssessment = (obtained / a.getMaxMarks()) * a.getWeightage();
            totalPercent += percentOfAssessment;
        }
        return totalPercent;
    }

    // ---- Grading policy (configurable here) ----
    public Grade deriveGrade(double percent) {
        // Example policy:
        if (percent >= 85.0) return new Grade("A+", 4.0, percent);
        if (percent >= 70.0) return new Grade("A", 3.5, percent);
        if (percent >= 60.0) return new Grade("B", 3.0, percent);
        if (percent >= 50.0) return new Grade("C", 2.0, percent);
        if (percent >= 40.0) return new Grade("D", 1.0, percent);
        return new Grade("F", 0.0, percent);
    }

    // ---- Publish grades for a course (compute for all registered students) ----
    public void publishGradesForCourse(String cid) throws InvalidInputException {
        Course c = courses.get(cid);
        if (c == null) throw new InvalidInputException("Course not found");
        List<Student> enrolled = students.values().stream()
                .filter(s -> s.isRegisteredFor(c))
                .collect(Collectors.toList());
        for (Student s : enrolled) {
            double totalPct = computeTotalPercent(s.getId(), cid);
            Grade g = deriveGrade(totalPct);
            s.getTranscript().addOrUpdateGrade(cid, g);
        }
        // Recompute GPA for all affected students
        for (Student s : enrolled) recalcGpaForStudent(s);
    }

    // ---- Recalc GPA (weighted by credits) ----
    public void recalcGpaForStudent(Student s) {
        Map<String, Grade> gmap = s.getTranscript().getGrades();
        double numerator = 0.0; double denom = 0.0;
        for (Map.Entry<String, Grade> e : gmap.entrySet()) {
            Course c = courses.get(e.getKey());
            if (c == null) continue;
            numerator += e.getValue().getGradePoint() * c.getCredits();
            denom += c.getCredits();
        }
        double gpa = (denom == 0.0) ? 0.0 : numerator/denom;
        s.getTranscript().setGpa(gpa);
    }

    // ---- Generate transcript display for a student (includes GPA) ----
    public void generateTranscriptForStudent(String sid) throws InvalidInputException {
        Student s = students.get(sid);
        if (s == null) throw new InvalidInputException("Student not found");
        // ensure GPA up to date
        recalcGpaForStudent(s);
        s.getTranscript().printTranscript(courses);
    }

    // ---- Assessment analytics for a course ----
    public void assessmentAnalytics(String cid) throws InvalidInputException {
        Course c = courses.get(cid);
        if (c == null) throw new InvalidInputException("Course not found");
        System.out.println("Analytics for " + c);
        for (Assessment a : c.getAssessments()) {
            int count = 0;
            double sumPercent = 0.0;
            int passCount = 0;
            for (Student s : students.values()) {
                if (!s.isRegisteredFor(c)) continue;
                String key = mkKey(s.getId(), cid, a.getId());
                MarkEntry me = marks.get(key);
                if (me == null) continue;
                count++;
                double pct = (me.getMarksObtained() / a.getMaxMarks()) * 100.0;
                sumPercent += pct;
                if (pct >= 40.0) passCount++;
            }
            double avg = (count==0) ? 0.0 : sumPercent/count;
            System.out.printf(" - %s: entries=%d, avg=%.2f%%, pass=%d\n", a.getName(), count, avg, passCount);
        }
    }

    // ---- Grade sheet per course (student grade list) ----
    public void gradeSheetForCourse(String cid) throws InvalidInputException {
        Course c = courses.get(cid);
        if (c == null) throw new InvalidInputException("Course not found");
        System.out.println("Grade Sheet for " + c);
        System.out.printf("%-10s %-25s %-12s %-10s\n", "StudentID", "StudentName", "Total(%)", "Grade");
        for (Student s : students.values()) {
            if (!s.isRegisteredFor(c)) continue;
            double pct = computeTotalPercent(s.getId(), cid);
            Grade g = deriveGrade(pct);
            System.out.printf("%-10s %-25s %-12.2f %-10s\n", s.getId(), s.getName(), pct, g.getLetter());
        }
    }

    // ---- Utility getters for menu ----
    public List<Course> listCourses() { return new ArrayList<>(courses.values()); }
    public List<Student> listStudents() { return new ArrayList<>(students.values()); }
    public Course getCourse(String cid) { return courses.get(cid); }
    public Student getStudent(String sid) { return students.get(sid); }

    // For debugging / show raw marks
    public void printAllMarks() {
        System.out.println("All recorded marks:");
        marks.values().forEach(m -> System.out.println(m));
    }
}
