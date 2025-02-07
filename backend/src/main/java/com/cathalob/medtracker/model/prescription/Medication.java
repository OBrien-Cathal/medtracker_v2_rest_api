package com.cathalob.medtracker.model.prescription;

import com.cathalob.medtracker.model.enums.DAYSTAGE;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "MEDICATION")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Medication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;


    public String nameWithDayStage(DAYSTAGE ds) {
        return getName() + " (" + ds.toString().charAt(0) + ds.toString().substring(1).toLowerCase() + ')';
    }

}
