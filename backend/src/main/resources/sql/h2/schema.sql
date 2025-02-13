create type if not exists USER_ROLE as enum('USER', 'PATIENT', 'PRACTITIONER','ADMIN' );
create type if not exists DAYSTAGE as enum( 'WAKEUP', 'MORNING','MIDDAY','AFTERNOON','EVENING','NIGHT','BEDTIME');

create table if not exists USERMODEL (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    username varchar(255) NOT NULL UNIQUE,
    password varchar(255) NOT NULL,
    role USER_ROLE NOT NULL
);

create table if not exists DAILYEVALUATION (
    record_Date date NOT NULL,
    userModel_id BIGINT NOT NULL,
    foreign key (userModel_id) references userModel(id),
    primary key (record_Date, userModel_id)
);


create table if not exists ACCOUNT_REGISTRATION (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    USERMODEL_ID BIGINT NOT NULL,
    foreign key (userModel_id) references userModel(id),
    CONFIRMED bit NOT NULL,
    REGISTRATION_ID uuid NOT NULL,
    REGISTRATION_TIME timestamp NOT NULL,
    CONFIRMATION_TIME timestamp
);
create table if not exists ROLECHANGE (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    new_role USER_ROLE NOT NULL,
    old_role USER_ROLE NOT NULL,
    userModel_id BIGINT NOT NULL,
    foreign key (userModel_id) references userModel(id),
    approved_by_id BIGINT,
    foreign key (approved_by_id) references userModel(id),
    REQUEST_TIME timestamp NOT NULL,
    APPROVAL_TIME timestamp
);

create table if not exists PATIENT_REGISTRATION (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    userModel_id BIGINT NOT NULL,
    foreign key (userModel_id) references userModel(id),
    practitioner_id BIGINT,
    foreign key (practitioner_id) references userModel(id),
    registered bit NOT NULL
);



create table if not exists MEDICATION (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name varchar(255) NOT NULL
);
create table if not exists PRESCRIPTION (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    medication_id BIGINT NOT NULL,
    foreign key (medication_id) references medication(id),
    dose_mg int NOT NULL,
    patient_id BIGINT NOT NULL,
    foreign key (patient_id) references userModel(id),
    practitioner_id BIGINT NOT NULL,
    foreign key (practitioner_id) references userModel(id),
    begin_time timestamp NOT NULL,
    end_time timestamp
);


create table if not exists PRESCRIPTIONSCHEDULEENTRY (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    day_stage DAYSTAGE NOT NULL,
    prescription_id BIGINT NOT NULL,
    foreign key (prescription_id) references prescription(id)
);

create table if not exists DOSE (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    dose_time timestamp NOT NULL,
    taken bit NOT NULL,
    dailyevaluation_record_Date date NOT NULL,
    dailyevaluation_userModel_id BIGINT NOT NULL,
    foreign key (dailyevaluation_record_Date, dailyevaluation_userModel_id) references dailyevaluation(record_Date,userModel_id),
    PRESCRIPTION_SCHEDULE_ENTRY_ID BIGINT NOT NULL,
    foreign key (PRESCRIPTION_SCHEDULE_ENTRY_ID) references prescriptionscheduleentry(id)
);

create table if not exists BLOODPRESSUREREADING (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    systole int NOT NULL,
    diastole int NOT NULL,
    heart_rate int NOT NULL,
    reading_time timestamp NOT NULL,
    dailyevaluation_record_Date date NOT NULL,
    dailyevaluation_userModel_id BIGINT NOT NULL,
    foreign key (dailyevaluation_record_Date, dailyevaluation_userModel_id) references dailyevaluation(record_Date,userModel_id),
    day_stage DAYSTAGE NOT NULL
);