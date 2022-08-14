package org.danylo.service;

import org.danylo.logging.Log;
import org.danylo.model.Trainer;
import org.danylo.model.WorkingTime;
import org.danylo.repository.WorkingTimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpSession;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class WorkingTimeService {
    private final WorkingTimeRepository workingTimeRepository;

    @Autowired
    public WorkingTimeService(WorkingTimeRepository workingTimeRepository) {
        this.workingTimeRepository = workingTimeRepository;
    }

    public WorkingTime buildWorkingTimeObject(Trainer trainer, DayOfWeek dayOfWeek, String hoursFrom, String hoursTo) {
        WorkingTime workingTime = new WorkingTime();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
        try {
            workingTime.setHoursFrom(new Time(sdf.parse(hoursFrom).getTime()));
            workingTime.setHoursTo(new Time(sdf.parse(hoursTo).getTime()));
        } catch (ParseException e) {
            Log.logger.error(e.getMessage());
        }
        workingTime.setTrainer(trainer);
        workingTime.setDayOfWeek(dayOfWeek);

        return workingTime;
    }

    public List<LocalTime> getWorkingHoursOfTrainer(WorkingTime workingTimeOfTrainerByDayOfWeek) {
        List<LocalTime> workingHours = new ArrayList<>();
        LocalTime startTime = LocalTime.parse(workingTimeOfTrainerByDayOfWeek.getHoursFrom().toString());
        LocalTime endTime = LocalTime.parse(workingTimeOfTrainerByDayOfWeek.getHoursTo().toString());
        int delta = endTime.getHour() - startTime.getHour();
        for (int i = 0; i < delta; i++) {
            workingHours.add(startTime.plusHours(i));
        }

        return workingHours;
    }

    public void setWorkingTimeInTrainerProfile(Trainer trainer, DayOfWeek dayOfWeek, HttpSession httpSession) {
        List<DayOfWeek> dayOfWeeks = Arrays.asList(DayOfWeek.values());
        WorkingTime workingTimeOfTrainerByDayOfWeek = workingTimeRepository.getWorkingTimeOfTrainerByDayOfWeek(trainer, dayOfWeek);

        httpSession.setAttribute("selectedDayOfWeek", dayOfWeek);
        httpSession.setAttribute("dayOfWeeks", dayOfWeeks);
        httpSession.setAttribute("workingTimeOfTrainerByDayOfWeek", workingTimeOfTrainerByDayOfWeek);
    }

}
