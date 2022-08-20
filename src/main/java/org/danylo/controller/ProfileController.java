package org.danylo.controller;

import org.danylo.model.Country;
import org.danylo.model.FitnessClub;
import org.danylo.model.Role;
import org.danylo.model.Trainer;
import org.danylo.model.User;
import org.danylo.model.WorkingTime;
import org.danylo.repository.FitnessClubRepository;
import org.danylo.repository.TrainerRepository;
import org.danylo.repository.UserRepository;
import org.danylo.repository.WorkingTimeRepository;
import org.danylo.service.CountryService;
import org.danylo.service.ExportFileService;
import org.danylo.service.FitnessClubService;
import org.danylo.service.TrainerService;
import org.danylo.service.UserService;
import org.danylo.service.WorkingTimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.time.DayOfWeek;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("profile")
public class ProfileController {
    UserService userService;
    UserRepository userRepository;
    TrainerRepository trainerRepository;
    TrainerService trainerService;
    CountryService countryService;
    WorkingTimeRepository workingTimeRepository;
    WorkingTimeService workingTimeService;
    FitnessClubRepository fitnessClubRepository;
    FitnessClubService fitnessClubService;
    ExportFileService fileService;

    @Autowired
    public ProfileController(UserService userService,
                             UserRepository userRepository,
                             TrainerRepository trainerRepository,
                             TrainerService trainerService,
                             CountryService countryService,
                             WorkingTimeRepository workingTimeRepository,
                             WorkingTimeService workingTimeService,
                             FitnessClubRepository fitnessClubRepository,
                             FitnessClubService fitnessClubService,
                             ExportFileService fileService) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.trainerRepository = trainerRepository;
        this.trainerService = trainerService;
        this.countryService = countryService;
        this.workingTimeRepository = workingTimeRepository;
        this.workingTimeService = workingTimeService;
        this.fitnessClubRepository = fitnessClubRepository;
        this.fitnessClubService = fitnessClubService;
        this.fileService = fileService;
    }

    @GetMapping()
    public String showProfilePage(Model model) {
        User currentUser = userService.getCurrentUser();
        model.addAttribute("currentUser", currentUser);
        String filename = "";

        if (currentUser.getRole() == Role.USER) {
           filename = "client/profile";
        } else if (currentUser.getRole() == Role.TRAINER) {
            Trainer currentTrainer = trainerService.getTrainerFromSession();
            trainerService.setTrainerData(Collections.singletonList(currentTrainer));
            List<WorkingTime> workingTimes = workingTimeRepository.getWorkingDaysOfTrainer(currentTrainer);
            model.addAttribute("currentTrainer", currentTrainer);
            model.addAttribute("workingTimes", workingTimes);
            filename = "trainer/profile";
        } else if (currentUser.getRole() == Role.CLUB) {
            FitnessClub fitnessClub = fitnessClubRepository.getClubByUser(currentUser);
            List<Trainer> clubTrainers = fitnessClubRepository.getTrainersByClub(fitnessClub);
            trainerService.setTrainerData(clubTrainers);
            model.addAttribute("clubTrainers", clubTrainers);
            filename = "club/profile";
        }

        return filename;
    }

    @PreAuthorize("hasAuthority('editTrainer')")
    @GetMapping({"/trainer/{id}/edit", "/trainer/edit"})
    public ModelAndView showProfileEditPage(@RequestParam(defaultValue = "MONDAY") DayOfWeek dayOfWeek,
                                            @RequestParam(required = false) Integer countryId,
                                            @PathVariable(required = false) Integer id,
                                            HttpSession httpSession) {
        User currentUser = userService.getCurrentUser();
        Trainer trainer = new Trainer();
        if (currentUser.getRole() == Role.TRAINER) {
            trainer = trainerService.getTrainerFromSession();
        } else if (currentUser.getRole() == Role.CLUB
                && fitnessClubService.hasAccessToTrainer(fitnessClubRepository.getClubByUser(currentUser), trainerRepository.getById(id))) {
            trainer = trainerRepository.getById(id);
            currentUser = userRepository.getUserByTrainer(trainer);
        }
        trainerService.setTrainerData(Collections.singletonList(trainer));
        userService.setUserDataInProfile(trainer, countryId, httpSession);
        workingTimeService.setWorkingTimeInTrainerProfile(trainer, dayOfWeek, httpSession);

        return new ModelAndView("trainer/editProfile")
                .addObject("trainer", trainer);
    }

    @PreAuthorize("hasAuthority('editTrainer')")
    @PostMapping({"/trainer/{id}/edit", "/trainer/edit"})
    public String editProfilePage(@PathVariable(required = false) Integer id,
                                  @ModelAttribute("trainer") @Valid Trainer trainer,
                                  BindingResult bindingResult,
                                  @ModelAttribute("dayOfWeek") DayOfWeek dayOfWeek,
                                  @ModelAttribute("hoursFrom") String hoursFrom,
                                  @ModelAttribute("hoursTo") String hoursTo,
                                  Model model,
                                  HttpSession httpSession) {
        if (bindingResult.hasFieldErrors()) {
            return "trainer/editProfile";
        }
        User currentUser = userService.getCurrentUser();
        Trainer currentTrainer = new Trainer();
        if (currentUser.getRole() == Role.TRAINER) {
            currentTrainer = trainerService.getTrainerFromSession();
        } else if (currentUser.getRole() == Role.CLUB
                && fitnessClubService.hasAccessToTrainer(fitnessClubRepository.getClubByUser(currentUser),
                                                         trainerRepository.getById(id))) {
            currentUser = userRepository.getUserByTrainer(trainer);
            currentTrainer = trainerRepository.getById(id);
        }
        int userId = currentUser.getId();
        trainer.setId(userId);
        trainer.setCountry((Country) httpSession.getAttribute("selectedCountry"));
        if (trainer.getPassword() == null) {
            trainerRepository.updateUserWithoutPassword(trainer);
        } else {
            trainerRepository.updateUser(trainer);
        }
        int trainerId = currentTrainer.getId();
        trainer.setId(trainerId);
        trainerRepository.updateTrainer(trainer);

        WorkingTime workingTime = workingTimeService.buildWorkingTimeObject(trainer, dayOfWeek, hoursFrom, hoursTo);
        if (workingTime.getHoursFrom() != null && workingTime.getHoursTo() != null) {
            workingTimeRepository.save(workingTime);
        } else if (workingTimeService.isTrainerWorkAtThisDay(workingTime) &&
                workingTime.getHoursFrom() == null && workingTime.getHoursTo() == null) {
            workingTimeRepository.deleteWorkingHoursOfTrainerByDayOfWeek(id, dayOfWeek);
        } else if ((workingTime.getHoursFrom() == null && workingTime.getHoursTo() != null) ||
                (workingTime.getHoursFrom() != null && workingTime.getHoursTo() == null)) {
            model.addAttribute("workingTimeMissing", "You should specify both hours from and hours to");
            return "trainer/editProfile";
        }

        return "redirect:/profile";
    }

    @PreAuthorize("hasAuthority('editUser')")
    @GetMapping("/user/edit")
    public ModelAndView showProfileEditPage(@ModelAttribute("user") User user,
                                            @RequestParam(required = false) Integer countryId,
                                            HttpSession httpSession) {
        User currentUser = userService.getCurrentUser();
        userService.setUserDataInProfile(currentUser, countryId, httpSession);

        return new ModelAndView("client/editProfile");
    }

    @PreAuthorize("hasAuthority('editUser')")
    @PostMapping(value = "/user/edit")
    public String editProfilePage(@ModelAttribute("user") @Valid User user,
                                  BindingResult bindingResult,
                                  HttpSession httpSession) {
        if (bindingResult.hasFieldErrors()) {
            return "client/editProfile";
        }
        User currentUser = (User) httpSession.getAttribute("currentUser");
        user.setId(currentUser.getId());
        user.setCountry((Country) httpSession.getAttribute("selectedCountry"));
        if (user.getPassword() == null) {
            userRepository.updateUserWithoutPassword(user);
        } else {
            userRepository.updateUser(user);
        }

        return "redirect:/profile";
    }

    @GetMapping("/export/pdf")
    public void exportToPdf(HttpServletResponse response) {
        User currentUser = userService.getCurrentUser();
        FitnessClub fitnessClub = fitnessClubRepository.getClubByUser(currentUser);
        List<Trainer> clubTrainers = fitnessClubRepository.getTrainersByClub(fitnessClub);
        trainerService.setTrainerData(clubTrainers);
        fileService.exportToPdf(clubTrainers, response);
    }

    @GetMapping("/export/excel")
    public void exportToExcel(HttpServletResponse response) {
        List<WorkingTime> workingDays = workingTimeRepository.getWorkingDaysOfTrainer(trainerService.getTrainerFromSession());
        fileService.exportToExcel(workingDays, response);
    }
}
