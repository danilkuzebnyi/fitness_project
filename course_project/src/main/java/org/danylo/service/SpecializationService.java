package org.danylo.service;

import org.danylo.model.Trainer;
import org.danylo.repository.SpecializationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SpecializationService {
    SpecializationRepository specializationRepository;

    @Autowired
    public SpecializationService(SpecializationRepository specializationRepository) {
        this.specializationRepository = specializationRepository;
    }

    public void setSpecializationsToTrainer(List<Trainer> trainers) {
        for (Trainer trainer : trainers) {
            trainer.setSpecializations(specializationRepository.getTrainerSpecializations(trainer.getId()));
        }
    }
}
