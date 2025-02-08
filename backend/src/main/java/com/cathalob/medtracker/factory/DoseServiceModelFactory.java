package com.cathalob.medtracker.factory;

import com.cathalob.medtracker.model.prescription.PrescriptionScheduleEntry;
import com.cathalob.medtracker.model.tracking.Dose;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DoseServiceModelFactory {

    public Map<LocalDate, List<Dose>> dummyDosesForRange(HashMap<LocalDate, List<PrescriptionScheduleEntry>> entries) {
        return DoseServiceModelFactory.DummyDosesForRange(entries);
    }

    public static Map<LocalDate, List<Dose>> DummyDosesForRange(HashMap<LocalDate, List<PrescriptionScheduleEntry>> entries) {
        HashMap<LocalDate, List<Dose>> dosesByDate = new HashMap<>();
        entries.forEach((key, value) -> dosesByDate.put(key, DoseServiceModelFactory.GetDummyDoses(value)));
        return dosesByDate;
    }

    public static List<Dose> GetDummyDoses(List<PrescriptionScheduleEntry> entries) {
        return entries.stream().map(DoseServiceModelFactory::GetDummyDose).toList();
    }

    public Dose getDummyDose(PrescriptionScheduleEntry entry) {
        return DoseServiceModelFactory.GetDummyDose(entry);

    }

    public static Dose GetDummyDose(PrescriptionScheduleEntry entry) {
        Dose dose = new Dose();
        dose.setTaken(true);
        dose.setPrescriptionScheduleEntry(entry);
        return dose;

    }

}
