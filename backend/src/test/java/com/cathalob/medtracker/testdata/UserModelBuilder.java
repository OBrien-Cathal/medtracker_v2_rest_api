package com.cathalob.medtracker.testdata;

import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.enums.USERROLE;

public class UserModelBuilder {

    private Long id;

    private String username = "user@user.com";
    private USERROLE role = USERROLE.USER;
    private String password = "abc";

    public UserModelBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public UserModelBuilder withUsername(String username) {
        this.username = username;
        return this;
    }

    public UserModelBuilder withPassword(String password) {
        this.password = password;
        return this;
    }

    public UserModelBuilder withRole(USERROLE role) {
        this.role = role;
        return this;
    }

    public UserModel build() {
        return new UserModel(id, username, password, role);
    }

    public static UserModelBuilder aUserModel() {
        return new UserModelBuilder();
    }

    public static UserModelBuilder aPatient() {
        return aUserModel().withRole(USERROLE.PATIENT).withUsername(patientName());
    }

    public static UserModelBuilder aPractitioner() {
        return aUserModel().withRole(USERROLE.PRACTITIONER).withUsername(practitionerName());
    }
    public static UserModelBuilder anAdmin() {
        return aUserModel().withRole(USERROLE.ADMIN).withUsername(adminName());
    }

    public static UserModelBuilder aNthPractitioner(int ordinal) {
        return aPractitioner().withUsername(practitionerName() + ordinal);
    }

    public static UserModelBuilder aNthPatient(int ordinal) {
        return aPatient().withUsername(patientName() + ordinal);
    }
    public static UserModelBuilder aNthUser(int ordinal) {
        return aUserModel().withUsername(userName() + ordinal);
    }


    public static String userName() {
        return "user@user.com";
    }

    public static String patientName() {
        return "patient@user.com";
    }

    public static String practitionerName() {
        return "practitioner@user.com";
    }

    public static String adminName() {
        return "admin@user.com";
    }


}
