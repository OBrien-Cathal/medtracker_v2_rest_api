package com.cathalob.medtracker.service.impl;

import com.cathalob.medtracker.mapper.DailyEvaluationMapper;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.tracking.DailyEvaluation;
import com.cathalob.medtracker.model.tracking.DailyEvaluationId;
import com.cathalob.medtracker.repository.DailyEvaluationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class EvaluationDataService {
    private final DailyEvaluationRepository dailyEvaluationRepository;
    private final DailyEvaluationMapper dailyEvaluationMapper;


    public void addDailyEvaluation(DailyEvaluation dailyEvaluation) {
        dailyEvaluationRepository.save(dailyEvaluation);
    }

    public Map<DailyEvaluationId, DailyEvaluation> getDailyEvaluationsById() {
        return dailyEvaluationRepository.findAll().stream().collect(Collectors.toMap(DailyEvaluation::getDailyEvaluationIdClass, Function.identity()));
    }

    public DailyEvaluation findForPatientAndDate(UserModel patient, LocalDate date) {
        return dailyEvaluationRepository.findById(new DailyEvaluationId(patient.getId(), date)).orElse(null);
    }

    public DailyEvaluation findOrCreateDailyEvaluationForPatientAndDate(UserModel patient, LocalDate date){

        DailyEvaluation found = findForPatientAndDate(patient, date);
        if(found != null) return found;

        return dailyEvaluationRepository.save(dailyEvaluationMapper.toDailyEvaluation(date, patient));
    }

    public List<DailyEvaluation> findDailyEvaluationsByUserModelActiveBetween(UserModel patient, LocalDate start, LocalDate end){
        return dailyEvaluationRepository.findDailyEvaluationsByUserModel(patient).stream()
                .filter(dailyEvaluation -> dailyEvaluation.isActiveBetween(start, end)).toList();
    }


}
