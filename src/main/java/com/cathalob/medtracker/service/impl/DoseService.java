package com.cathalob.medtracker.service.impl;

import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.tracking.Dose;
import com.cathalob.medtracker.repository.DoseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class DoseService {
    private final DoseRepository doseRepository;

    public DoseService(DoseRepository doseRepository) {
        this.doseRepository = doseRepository;
    }

    public List<Dose> getDoses(UserModel userModel) {
        return doseRepository.findDosesForUserId(userModel.getId());
    }

    public void saveDoses(List<Dose> newDoses) {
        doseRepository.saveAll(newDoses);
    }

    public Map<Long, Dose> getDosesById() {
        return doseRepository.findAll().stream().collect(Collectors.toMap(Dose::getId, Function.identity()));
    }

}
