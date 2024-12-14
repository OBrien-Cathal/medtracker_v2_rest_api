package com.cathalob.medtracker.service.impl;

import com.cathalob.medtracker.model.tracking.DailyEvaluation;
import com.cathalob.medtracker.model.tracking.DailyEvaluationId;
import com.cathalob.medtracker.repository.DailyEvaluationRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
public class EvaluationDataService {
    private final DailyEvaluationRepository dailyEvaluationRepository;

    public EvaluationDataService(DailyEvaluationRepository dailyEvaluationRepository) {

        this.dailyEvaluationRepository = dailyEvaluationRepository;
    }

    public void addDailyEvaluation(DailyEvaluation dailyEvaluation) {
        dailyEvaluationRepository.save(dailyEvaluation);
    }

    public Map<DailyEvaluationId, DailyEvaluation> getDailyEvaluationsById() {
        return dailyEvaluationRepository.findAll().stream().collect(Collectors.toMap(DailyEvaluation::getDailyEvaluationIdClass, Function.identity()));
    }
}
