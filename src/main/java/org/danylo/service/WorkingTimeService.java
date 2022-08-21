package org.danylo.service;

import org.danylo.logging.Log;
import org.danylo.model.Trainer;
import org.danylo.model.WorkingTime;
import org.danylo.repository.WorkingTimeRepository;
import org.danylo.web.Dialog;
import org.danylo.web.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
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
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
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

    public boolean isTrainerWorkAtThisDay(WorkingTime workingTime) {
        return workingTimeRepository.getWorkingTimeOfTrainerByDayOfWeek(workingTime).getHoursFrom() != null;
    }

    public boolean validateTime(WorkingTime workingTime, Model model) {
        boolean isAnyErrors = false;
        if ((workingTime.getHoursFrom() == null && workingTime.getHoursTo() != null) ||
                (workingTime.getHoursFrom() != null && workingTime.getHoursTo() == null)) {
            Dialog.create(model, Message.WORKING_TIME_SHOULD_BE_DEFINED);
            isAnyErrors = true;
        } else if (workingTime.getHoursFrom() != null && workingTime.getHoursTo() != null &&
                workingTime.getHoursFrom().getTime() > workingTime.getHoursTo().getTime()) {
            Dialog.create(model, Message.TIME_VALIDATION);
            isAnyErrors = true;
        }

        return isAnyErrors;
    }

    public void changeWorkingTimeOfTrainer(WorkingTime workingTime, int trainerId, DayOfWeek dayOfWeek) {
        if (workingTime.getHoursFrom() != null && workingTime.getHoursTo() != null) {
            workingTimeRepository.save(workingTime);
        } else if (isTrainerWorkAtThisDay(workingTime) &&
                workingTime.getHoursFrom() == null && workingTime.getHoursTo() == null) {
            workingTimeRepository.deleteWorkingHoursOfTrainerByDayOfWeek(trainerId, dayOfWeek);
        }
    }
}
