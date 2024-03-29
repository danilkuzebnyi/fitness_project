package org.danylo.controller;

import org.danylo.model.Rating;
import org.danylo.model.Specialization;
import org.danylo.model.Trainer;
import org.danylo.model.WorkingTime;
import org.danylo.repository.SpecializationRepository;
import org.danylo.repository.TrainerRepository;
import org.danylo.repository.WorkingTimeRepository;
import org.danylo.service.SpecializationService;
import org.danylo.service.TrainerService;
import org.danylo.service.UserService;
import org.danylo.service.WorkingTimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/trainers")
public class TrainerController {
    UserService userService;
    TrainerRepository trainerRepository;
    TrainerService trainerService;
    SpecializationRepository specializationRepository;
    SpecializationService specializationService;
    WorkingTimeRepository workingTimeRepository;
    WorkingTimeService workingTimeService;

    @Autowired
    public TrainerController(UserService userService,
                             TrainerRepository trainerRepository,
                             TrainerService trainerService,
                             SpecializationRepository specializationRepository,
                             SpecializationService specializationService,
                             WorkingTimeRepository workingTimeRepository,
                             WorkingTimeService workingTimeService) {
        this.userService = userService;
        this.trainerRepository = trainerRepository;
        this.trainerService = trainerService;
        this.specializationRepository = specializationRepository;
        this.specializationService = specializationService;
        this.workingTimeRepository = workingTimeRepository;
        this.workingTimeService = workingTimeService;
    }

    @GetMapping()
    public ModelAndView showAllTrainers(@RequestParam(defaultValue = "all") String specialization,
                                        @RequestParam(defaultValue = "") String sorting) {
        List<Trainer> trainers;
        trainers = trainerService.getFilteredTrainers(specialization);
        trainers = trainerService.getSortedTrainers(sorting, trainers);
        trainerService.setTrainerData(trainers);
        specializationService.setSpecializationsToTrainer(trainers);
        List<Specialization> specializations = specializationRepository.showAllSpecializations();

        return new ModelAndView("trainer/trainers")
                .addObject("trainers", trainers)
                .addObject("specialization", specialization)
                .addObject("specializations", specializations);
    }

    @GetMapping("/{id:[\\d]+}")
    public ModelAndView showTrainerPage(@PathVariable Integer id,
                                        @RequestParam(required = false) String date) {
        Trainer trainer = trainerRepository.getById(id);
        trainerService.setTrainerData(Collections.singletonList(trainer));
        specializationService.setSpecializationsToTrainer(Collections.singletonList(trainer));
        Rating rating = trainer.getRating();

        DayOfWeek dayOfWeek;
        WorkingTime workingTimeOfTrainerByDayOfWeek = null;
        List<LocalTime> workingHours = new ArrayList<>();
        if (date != null && !date.isEmpty()) {
            LocalDate parsedDate = LocalDate.parse(date);
            dayOfWeek = parsedDate.getDayOfWeek();
            workingTimeOfTrainerByDayOfWeek = workingTimeRepository.getWorkingTimeOfTrainerByDayOfWeek(trainer, dayOfWeek);
            if (workingTimeOfTrainerByDayOfWeek.getDayOfWeek() != null) {
                workingHours = workingTimeService.getWorkingHoursOfTrainer(workingTimeOfTrainerByDayOfWeek);
            }
        }

        return new ModelAndView("trainer/trainer")
                .addObject("trainer", trainer)
                .addObject("workingTimeOfTrainerByDayOfWeek", workingTimeOfTrainerByDayOfWeek)
                .addObject("workingHours", workingHours)
                .addObject("date", date)
                .addObject("rating", rating);
    }

    @PostMapping("/{id:[\\d]+}")
    @ResponseBody
    public ResponseEntity<String> updateTrainerPage(@PathVariable Integer id,
                                                    @RequestParam Integer currentRating) {
        ResponseEntity<String> response;
        try {
            trainerService.saveRating(id, currentRating);
            response = ResponseEntity.ok("Rating has been submitted successfully");
        } catch (Exception e) {
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        return response;
    }

    @PreAuthorize("hasAuthority('book')")
    @GetMapping("/{id:[\\d]+}/success")
    public ModelAndView getSuccessPage(@PathVariable Integer id) {
        return new ModelAndView("trainer/success");
    }

    @PostMapping("/{id:[\\d]+}/success")
    public ModelAndView bookWorkout(@PathVariable Integer id,
                                    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime selectedTime) {
        trainerService.bookClientWithTrainer(userService.getCurrentUser().getId(), id, date, selectedTime);

        return new ModelAndView("trainer/success");
    }
}
