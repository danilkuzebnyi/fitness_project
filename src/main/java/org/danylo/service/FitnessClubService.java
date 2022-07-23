package org.danylo.service;

import org.danylo.model.FitnessClub;
import org.danylo.model.Trainer;
import org.danylo.repository.FitnessClubRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FitnessClubService {
    FitnessClubRepository fitnessClubRepository;

    @Autowired
    public FitnessClubService(FitnessClubRepository fitnessClubRepository) {
        this.fitnessClubRepository = fitnessClubRepository;
    }

    public boolean hasAccessToTrainer(FitnessClub fitnessClub, Trainer trainer) {
        List<Trainer> trainersByClub = fitnessClubRepository.getTrainersByClub(fitnessClub);
        return trainersByClub.stream().anyMatch(t -> t.getId() == trainer.getId());
    }
}
