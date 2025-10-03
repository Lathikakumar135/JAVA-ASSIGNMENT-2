package model;

public class Assessment {
    private String id;
    private String name; // e.g., "Midterm", "Quiz1"
    private double weightage; // percentage of final grade (0-100)
    private double maxMarks;

    public Assessment(String id, String name, double weightage, double maxMarks) {
        this.id = id.trim();
        this.name = name.trim();
        this.weightage = weightage;
        this.maxMarks = maxMarks;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public double getWeightage() { return weightage; }
    public double getMaxMarks() { return maxMarks; }

    @Override
    public String toString() {
        return id + " - " + name + " [" + weightage + "%, max=" + maxMarks + "]";
    }
}
