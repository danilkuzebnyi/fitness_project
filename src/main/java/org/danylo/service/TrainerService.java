package org.danylo.service;

import org.danylo.model.Sorting;
import org.danylo.model.Trainer;
import org.danylo.model.User;
import org.danylo.repository.TrainerRepository;
import org.danylo.repository.UserRepository;
import org.danylo.web.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TrainerService {
    private final TrainerRepository trainerRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    @Autowired
    public TrainerService(TrainerRepository trainerRepository,
                          UserRepository userRepository,
                          UserService userService) {
        this.trainerRepository = trainerRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    public Trainer getTrainerFromSession() {
        User currentUser = userService.getCurrentUser();
        return trainerRepository.getTrainerByUser(currentUser);
    }

    public void setTrainerData(List<Trainer> trainers) {
        for (Trainer trainer : trainers) {
            User user = userRepository.getUserByTrainer(trainer);
            trainer.setFirstName(user.getFirstName());
            trainer.setLastName(user.getLastName());
            trainer.setCountry(user.getCountry());
            trainer.setTelephoneNumber(user.getTelephoneNumber());
            trainer.setUsername(user.getUsername());
            trainer.setPassword(user.getPassword());
        }
    }

    public List<Trainer> sortByExperience(List<Trainer> trainers) {
        return trainers.stream()
                .sorted(Comparator.comparing(Trainer::getExperience))
                .collect(Collectors.toList());
    }

    public List<Trainer> sortByIncreasingPrice(List<Trainer> trainers) {
        return trainers.stream()
                .sorted(Comparator.comparing(Trainer::getPrice))
                .collect(Collectors.toList());
    }

    public List<Trainer> sortByDecreasingPrice(List<Trainer> trainers) {
        return trainers.stream()
                .sorted(Comparator.comparing(Trainer::getPrice).reversed())
                .collect(Collectors.toList());
    }

    public List<Trainer> sortByRating(List<Trainer> trainers) {
        return trainers.stream()
                .sorted((trainer1, trainer2) ->
                        Double.compare(trainer2.getRating().getValue(), trainer1.getRating().getValue()))
                .collect(Collectors.toList());
    }

    public List<Trainer> getSortedTrainers(String sorting, List<Trainer> trainers) {
        switch (Sorting.fromValue(sorting)) {
            case EXPERIENCE:
                trainers = sortByExperience(trainers);
                break;
            case SMALL_PRICE:
                trainers = sortByIncreasingPrice(trainers);
                break;
            case BIG_PRICE:
                trainers = sortByDecreasingPrice(trainers);
                break;
            case RATING:
                trainers = sortByRating(trainers);
                break;
            default:
                break;
        }
        return trainers;
    }

    public List<Trainer> getFilteredTrainers(String specialization) {
        List<Trainer> trainers;
        if (specialization.equals("all")) {
            trainers = trainerRepository.showAll();
        } else {
            trainers = trainerRepository.showTrainersBySpecialization(specialization);
        }
        return trainers;
    }

    public void saveRating(int trainerId, int currentRating) {
        User currentUser = userService.getCurrentUser();
        if (isUserRatedTrainer(currentUser, trainerId)) {
            throw new RuntimeException(Message.ALREADY_RATE_TRAINER.toString());
        } else if (!isUserTrainedWithTrainer(currentUser, trainerId)) {
            throw new RuntimeException(Message.CANNOT_RATE_TRAINER.toString());
        } else {
            trainerRepository.saveRating(trainerId, currentUser.getId(), currentRating);
        }
    }

    public boolean isUserRatedTrainer(User user, int trainerId) {
        return trainerRepository.getTrainersThatUserRated(user.getId()).contains(trainerId);
    }

    public boolean isUserTrainedWithTrainer(User user, int trainerId) {
        return trainerRepository.getUsersWhoTrainedWithTrainer(trainerId).contains(user.getId());
    }

    public void bookClientWithTrainer(int userId, int trainerId, LocalDate date, LocalTime time) {
        LocalDateTime meetingTime = LocalDateTime.of(date, time);
        trainerRepository.bookClientWithTrainer(userId, trainerId, meetingTime);
    }

    public boolean isReliable(Trainer trainer) {
        return trainerRepository.getNumberOfClients(trainer) > 5;
    }
}
