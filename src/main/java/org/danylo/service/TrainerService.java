package org.danylo.service;

import org.danylo.model.Sorting;
import org.danylo.model.Trainer;
import org.danylo.model.User;
import org.danylo.repository.TrainerRepository;
import org.danylo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class TrainerService {
    TrainerRepository trainerRepository;
    UserRepository userRepository;
    UserService userService;

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
                .sorted((Trainer trainer1, Trainer trainer2) ->
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

    public boolean isUserTrainedWithTrainer (User user, int trainerId) {
        return trainerRepository.getUsersWhoTrainedWithTrainer(trainerId).contains(user.getId());
    }

    public boolean isReliable(Trainer trainer) {
        return trainerRepository.getNumberOfClients(trainer) > 5;
    }
}
