package org.danylo.model;

import java.sql.Time;
import java.time.DayOfWeek;

public class WorkingTime {
    private int id;
    private Trainer trainer;
    private DayOfWeek dayOfWeek;
    private Time hoursFrom;
    private Time hoursTo;

    public  WorkingTime() {
    }

    public WorkingTime(int id, Trainer trainer, DayOfWeek dayOfWeek, Time hoursFrom, Time hoursTo) {
        this.id = id;
        this.trainer = trainer;
        this.dayOfWeek = dayOfWeek;
        this.hoursFrom = hoursFrom;
        this.hoursTo = hoursTo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Trainer getTrainer() {
        return trainer;
    }

    public void setTrainer(Trainer trainer) {
        this.trainer = trainer;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public Time getHoursFrom() {
        return hoursFrom;
    }

    public void setHoursFrom(Time hoursFrom) {
        this.hoursFrom = hoursFrom;
    }

    public Time getHoursTo() {
        return hoursTo;
    }

    public void setHoursTo(Time hoursTo) {
        this.hoursTo = hoursTo;
    }

    @Override
    public String toString() {
        return "WorkingTime{" +
                " trainer=" + trainer +
                ", dayOfWeek=" + dayOfWeek +
                ", hoursFrom=" + hoursFrom +
                ", hoursTo=" + hoursTo +
                '}';
    }
}
