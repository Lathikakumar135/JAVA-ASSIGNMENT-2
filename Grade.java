package model;

public class Grade {
    private String letter;
    private double gradePoint;
    private double totalPercent;

    public Grade(String letter, double gradePoint, double totalPercent) {
        this.letter = letter;
        this.gradePoint = gradePoint;
        this.totalPercent = totalPercent;
    }

    public String getLetter() { return letter; }
    public double getGradePoint() { return gradePoint; }
    public double getTotalPercent() { return totalPercent; }

    @Override
    public String toString() {
        return letter + " (" + String.format("%.2f", totalPercent) + "%, gp=" + gradePoint + ")";
    }
}
