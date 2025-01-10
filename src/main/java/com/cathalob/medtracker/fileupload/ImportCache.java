package com.cathalob.medtracker.fileupload;

import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.prescription.Medication;
import com.cathalob.medtracker.model.prescription.Prescription;
import com.cathalob.medtracker.model.prescription.PrescriptionScheduleEntry;
import com.cathalob.medtracker.model.tracking.DailyEvaluation;
import com.cathalob.medtracker.model.tracking.DailyEvaluationId;
import com.cathalob.medtracker.model.tracking.Dose;
import com.cathalob.medtracker.service.impl.DoseService;
import com.cathalob.medtracker.service.impl.EvaluationDataService;

import com.cathalob.medtracker.service.impl.PrescriptionsService;
import com.cathalob.medtracker.service.impl.UserServiceImpl;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Getter
public class ImportCache {
    private Map<Long, UserModel> userModels;

    private Map<Long, Medication> medications;

    private Map<Long, Prescription> prescriptions;

    private Map<Long, PrescriptionScheduleEntry> prescriptionScheduleEntries;

    private Map<Long, Dose> doses;

    private Map<DailyEvaluationId, DailyEvaluation> dailyEvaluations;

    @Setter
    private UserModel userModel;


    public ImportCache() {
        this.dailyEvaluations = new HashMap<>();
        this.prescriptionScheduleEntries = new HashMap<>();
        this.doses = new HashMap<>();
    }

    public DailyEvaluation getDailyEvaluation(LocalDate localDate){
        return dailyEvaluations.get(this.getDailyEvaluationKey(localDate, userModel));
    }
    public DailyEvaluationId getDailyEvaluationKey(LocalDate localDate, UserModel userModel) {
        return new DailyEvaluationId(userModel.getId(), localDate);
    }
    public PrescriptionScheduleEntry getPrescriptionScheduleEntry(Long key){
        return prescriptionScheduleEntries.get(key);
    }

    public void ensureDailyEvaluations(List<LocalDate> dates, EvaluationDataService evaluationDataService) {
        dates.forEach(localDate -> {
                DailyEvaluationId dailyEvaluationKey = this.getDailyEvaluationKey(localDate, userModel);
                if (!dailyEvaluations.containsKey(dailyEvaluationKey)) {
                    DailyEvaluation dailyEvaluation = new DailyEvaluation();
                    dailyEvaluation.setRecordDate(localDate);
                    dailyEvaluation.setUserModel(userModel);
                    evaluationDataService.addDailyEvaluation(dailyEvaluation);
                    dailyEvaluations.put(dailyEvaluationKey, dailyEvaluation);
            }
        });

    }

    public void loadPrescriptions(PrescriptionsService prescriptionsService) {
        if (prescriptions != null && !prescriptions.keySet().isEmpty()) return;
        prescriptions = prescriptionsService.getPrescriptionsById();
    }

    public void loadPrescriptionScheduleEntries(PrescriptionsService prescriptionsService) {
        if (prescriptionScheduleEntries != null && !prescriptionScheduleEntries.keySet().isEmpty()) return;
        prescriptionScheduleEntries = prescriptionsService.getPrescriptionScheduleEntriesById();
    }

    public void loadDailyEvaluations(EvaluationDataService evaluationDataService) {
        if (dailyEvaluations != null && !dailyEvaluations.keySet().isEmpty()) return;
        dailyEvaluations = evaluationDataService.getDailyEvaluationsById();
    }

    public void loadDoses(DoseService doseService) {
        if (doses != null && !doses.keySet().isEmpty()) return;
        doses = doseService.getDosesById();
    }
    public void loadUserModels(UserServiceImpl userService) {
        if (userModels != null && !userModels.keySet().isEmpty()) return;
        userModels = userService.getUserModelsById();
    }
    public void loadMedications(PrescriptionsService prescriptionsService) {
        if (medications != null && !medications.keySet().isEmpty()) return;
        medications = prescriptionsService.getMedicationsById();
    }
}
