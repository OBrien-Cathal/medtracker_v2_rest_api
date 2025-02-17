package com.cathalob.medtracker.factory;

import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.enums.USERROLE;
import com.cathalob.medtracker.model.userroles.RoleChange;

import java.time.LocalDateTime;

public class RoleChangeServiceFactory {

    public RoleChange roleChange(UserModel userModel, USERROLE userrole) {
        return RoleChange(userModel, userrole);
    }

    public static RoleChange RoleChange(UserModel userModel, USERROLE userrole) {

        RoleChange roleChange = new RoleChange();
        roleChange.setNewRole(userrole);
        roleChange.setUserModel(userModel);
        roleChange.setOldRole(userModel.getRole());
        roleChange.setRequestTime(LocalDateTime.now());
        return roleChange;
    }


}
