package com.cathalob.medtracker.controller;

import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.enums.DAYSTAGE;
import com.cathalob.medtracker.model.tracking.BloodPressureReading;
import com.cathalob.medtracker.service.impl.BloodPressureDataService;
import com.cathalob.medtracker.service.impl.PatientsService;
import com.cathalob.medtracker.service.impl.UserServiceImpl;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
public class PatientController {
    private final PatientsService patientsService;
    private final BloodPressureDataService bloodPressureDataService;
    private final UserServiceImpl userService;

    public PatientController(PatientsService patientsService, BloodPressureDataService bloodPressureDataService, UserServiceImpl userService) {
        this.patientsService = patientsService;
        this.bloodPressureDataService = bloodPressureDataService;
        this.userService = userService;
    }


    private UserModel getUserModel(Authentication authentication) {
        return userService.findByLogin(authentication.getName());
    }
    @GetMapping("/patient/graphs")
    public String graphs(Model model, Authentication authentication) {
        {
            model.addAttribute("graphPageTitle", "Data Visualizations");
            model.addAttribute("bpSectionTitle", "Blood pressure");
            model.addAttribute("col0", "Date");
            UserModel userModel = this.getUserModel(authentication);
            List<BloodPressureReading> bloodPressureReadings = bloodPressureDataService.getBloodPressureReadings(userModel);
            List<DAYSTAGE> daystageList = bloodPressureReadings.stream().map(BloodPressureReading::getDayStage)
                    .distinct()
                    .toList();
            model.addAttribute("systoleGraphTitle", "Systole");
            model.addAttribute("systoleColls", patientsService.getBloodPressureGraphColumnNames(daystageList, List.of("Danger High", "High Stage 1", "Elevated")));
            model.addAttribute("systoleChartData", patientsService.getSystoleGraphData(bloodPressureReadings));

            model.addAttribute("diastoleGraphTitle", "Diastole");
            model.addAttribute("diastoleColls", patientsService.getBloodPressureGraphColumnNames(daystageList, List.of("Danger High", "High Stage 1")));
            model.addAttribute("diastoleChartData", patientsService.getDiastoleGraphData(bloodPressureReadings));

            model.addAttribute("doseGraphTitle", "Dose (mg)");
            model.addAttribute("colls", (patientsService.getDoseGraphColumnNames(userModel)));
            List<List<Object>> doseGraphData = patientsService.getDoseGraphData(userModel);
            model.addAttribute("doseChartData", doseGraphData);

            return "graphs";
        }
    }
}
