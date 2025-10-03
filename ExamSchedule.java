package model;

import java.time.LocalDate;

public class ExamSchedule {
    private LocalDate date;
    private String venue;

    public ExamSchedule(LocalDate date, String venue) {
        this.date = date;
        this.venue = venue;
    }

    public LocalDate getDate() { return date; }
    public String getVenue() { return venue; }

    @Override
    public String toString() {
        return date + " @ " + venue;
    }
}
