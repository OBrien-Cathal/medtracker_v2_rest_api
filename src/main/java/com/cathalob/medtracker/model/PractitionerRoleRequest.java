package com.cathalob.medtracker.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name="PRACTITIONERROLEREQUEST")
@Data
public class PractitionerRoleRequest {
    @Id
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "USERMODEL_ID", nullable = false)
    @JsonIgnore
    private UserModel userModel;

    private boolean approved;

    public PractitionerRoleRequest() {
        boolean approved = false;
    }

    public boolean isPending(){
        return !isApproved();
    }
}
