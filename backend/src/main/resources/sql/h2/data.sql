INSERT INTO USERMODEL (username, password, role)
SELECT *
FROM (VALUES
    ROW('admin@medtracker.com', '$2a$10$WSU4n.NhUE7g1lwMAeTT9OXAaGJG2s.4UkhYIYuIcT0qn0AxNV8NO', 'ADMIN'),
ROW('doc1@medtracker.com', '$2a$10$WSU4n.NhUE7g1lwMAeTT9OXAaGJG2s.4UkhYIYuIcT0qn0AxNV8NO', 'PRACTITIONER'),
ROW('patient1@medtracker.com', '$2a$10$WSU4n.NhUE7g1lwMAeTT9OXAaGJG2s.4UkhYIYuIcT0qn0AxNV8NO', 'PATIENT'),
ROW('patient2@medtracker.com', '$2a$10$WSU4n.NhUE7g1lwMAeTT9OXAaGJG2s.4UkhYIYuIcT0qn0AxNV8NO', 'PATIENT'),
ROW('user1@medtracker.com', '$2a$10$WSU4n.NhUE7g1lwMAeTT9OXAaGJG2s.4UkhYIYuIcT0qn0AxNV8NO', 'USER'),
ROW('user2@medtracker.com', '$2a$10$WSU4n.NhUE7g1lwMAeTT9OXAaGJG2s.4UkhYIYuIcT0qn0AxNV8NO', 'USER'),
ROW('user3@medtracker.com', '$2a$10$WSU4n.NhUE7g1lwMAeTT9OXAaGJG2s.4UkhYIYuIcT0qn0AxNV8NO', 'USER'),
ROW('user4@medtracker.com', '$2a$10$WSU4n.NhUE7g1lwMAeTT9OXAaGJG2s.4UkhYIYuIcT0qn0AxNV8NO', 'USER'),
ROW('user5@medtracker.com', '$2a$10$WSU4n.NhUE7g1lwMAeTT9OXAaGJG2s.4UkhYIYuIcT0qn0AxNV8NO', 'USER'),
ROW('user6@medtracker.com', '$2a$10$WSU4n.NhUE7g1lwMAeTT9OXAaGJG2s.4UkhYIYuIcT0qn0AxNV8NO', 'USER'),
ROW('user7@medtracker.com', '$2a$10$WSU4n.NhUE7g1lwMAeTT9OXAaGJG2s.4UkhYIYuIcT0qn0AxNV8NO', 'USER'),
ROW('user8@medtracker.com', '$2a$10$WSU4n.NhUE7g1lwMAeTT9OXAaGJG2s.4UkhYIYuIcT0qn0AxNV8NO', 'USER'),
ROW('doc2@medtracker.com', '$2a$10$WSU4n.NhUE7g1lwMAeTT9OXAaGJG2s.4UkhYIYuIcT0qn0AxNV8NO', 'PRACTITIONER'),
ROW('doc3@medtracker.com', '$2a$10$WSU4n.NhUE7g1lwMAeTT9OXAaGJG2s.4UkhYIYuIcT0qn0AxNV8NO', 'PRACTITIONER')) source_data
WHERE NOT EXISTS (SELECT NULL FROM USERMODEL);

INSERT INTO ACCOUNT_REGISTRATION (USERMODEL_ID, CONFIRMED, REGISTRATION_ID, REGISTRATION_TIME, CONFIRMATION_TIME)
SELECT *
FROM (VALUES
    ROW('1', 'TRUE', '4fd1b3de-3f98-4cc1-bee4-13948f6b7b27', {ts '2024-12-01 10:34:53'}, {ts '2024-12-01 10:34:53'}),
ROW('2', 'TRUE', '4fd1b3de-3f98-4cc1-bee4-13948f6b7b27', {ts '2024-12-01 10:34:53'}, {ts '2024-12-01 10:34:53'}),
ROW('3', 'TRUE', '4fd1b3de-3f98-4cc1-bee4-13948f6b7b27', {ts '2024-12-01 10:34:53'}, {ts '2024-12-01 10:34:53'}),
ROW('4', 'TRUE', '4fd1b3de-3f98-4cc1-bee4-13948f6b7b27', {ts '2024-12-01 10:34:53'}, {ts '2024-12-01 10:34:53'}),
ROW('5', 'TRUE', '4fd1b3de-3f98-4cc1-bee4-13948f6b7b27', {ts '2024-12-01 10:34:53'}, {ts '2024-12-01 10:34:53'}),
ROW('6', 'TRUE', '4fd1b3de-3f98-4cc1-bee4-13948f6b7b27', {ts '2024-12-01 10:34:53'}, {ts '2024-12-01 10:34:53'}),
ROW('7', 'FALSE', '4fd1b3de-3f98-4cc1-bee4-13948f6b7b27', {ts '2024-12-01 10:34:53'}, {ts '2024-12-01 10:34:53'}),
ROW('8', 'FALSE', '4fd1b3de-3f98-4cc1-bee4-13948f6b7b27', {ts '2024-12-01 10:34:53'}, {ts '2024-12-01 10:34:53'}),
ROW('9', 'TRUE', '4fd1b3de-3f98-4cc1-bee4-13948f6b7b27', {ts '2024-12-01 10:34:53'}, {ts '2024-12-01 10:34:53'}),
ROW('10', 'TRUE', '4fd1b3de-3f98-4cc1-bee4-13948f6b7b27', {ts '2024-12-01 10:34:53'}, {ts '2024-12-01 10:34:53'}),
ROW('11', 'TRUE', '4fd1b3de-3f98-4cc1-bee4-13948f6b7b27', {ts '2024-12-01 10:34:53'}, {ts '2024-12-01 10:34:53'}),
ROW('12', 'TRUE', '4fd1b3de-3f98-4cc1-bee4-13948f6b7b27', {ts '2024-12-01 10:34:53'}, {ts '2024-12-01 10:34:53'}),
ROW('13', 'TRUE', '4fd1b3de-3f98-4cc1-bee4-13948f6b7b27', {ts '2024-12-01 10:34:53'}, {ts '2024-12-01 10:34:53'}),
ROW('14', 'TRUE', '4fd1b3de-3f98-4cc1-bee4-13948f6b7b27', {ts '2024-12-01 10:34:53'}, {ts '2024-12-01 10:34:53'})) source_data
WHERE NOT EXISTS (SELECT NULL FROM ACCOUNT_REGISTRATION);


INSERT INTO ACCOUNT_DETAILS (FIRST_NAME, SURNAME, USERMODEL_ID)
SELECT *
FROM (VALUES
    ROW('FirstName', 'Surname', 1),
          ROW('FirstName', 'Surname',  2),
          ROW('FirstName', 'Surname', 3),
          ROW('FirstName', 'Surname',  4),
          ROW('FirstName', 'Surname',  5),
          ROW('FirstName', 'Surname',  6),
          ROW('FirstName', 'Surname',  7),
          ROW('FirstName', 'Surname',  8),
          ROW('FirstName', 'Surname',  9),
          ROW('FirstName', 'Surname',  10),
          ROW('FirstName', 'Surname',  11),
          ROW('FirstName', 'Surname',  12),
          ROW('FirstName', 'Surname',  13),
          ROW('FirstName', 'Surname',  14)
         ) source_data
WHERE NOT EXISTS (SELECT NULL FROM ACCOUNT_DETAILS);



INSERT INTO ROLECHANGE (NEW_ROLE, OLD_ROLE, USERMODEL_ID, REQUEST_TIME, APPROVED_BY_ID, APPROVAL_TIME)
SELECT *
FROM (VALUES
    ROW('ADMIN', 'USER', '1', {ts '2024-12-01 10:34:53'}, '1', {ts '2024-12-01 10:34:53'}),
ROW('PRACTITIONER', 'USER', '2', {ts '2024-12-01 10:34:53'}, '1', {ts '2024-12-01 10:34:53'}),
ROW('PATIENT', 'USER', '3', {ts '2024-12-01 10:34:53'}, '2', {ts '2024-12-01 10:34:53'}),
ROW('PATIENT', 'USER', '4', {ts '2024-12-01 10:34:53'}, '2', {ts '2024-12-01 10:34:53'}),

ROW('ADMIN', 'USER', '8', {ts '2024-12-01 10:34:53'}, NULL, NULL),
ROW('PRACTITIONER', 'USER', '8', {ts '2024-12-01 10:34:53'}, NULL, NULL),

ROW('PRACTITIONER', 'USER', '9', {ts '2024-12-01 10:34:53'}, NULL, NULL),

ROW('ADMIN', 'USER', '10', {ts '2024-12-01 10:34:53'}, NULL, NULL),

ROW('PRACTITIONER', 'USER', '13', {ts '2024-12-01 10:34:53'}, '1', {ts '2024-12-01 10:34:53'}),
ROW('PRACTITIONER', 'USER', '14', {ts '2024-12-01 10:34:53'}, '1', {ts '2024-12-01 10:34:53'})) source_data
WHERE NOT EXISTS (SELECT NULL FROM ROLECHANGE);

INSERT INTO PATIENT_REGISTRATION (USERMODEL_ID, PRACTITIONER_ID, REGISTERED)
SELECT *
FROM (VALUES
    ROW('3', '2', 'TRUE'),
ROW('4', '2', 'TRUE'),
ROW('11', '2', 'FALSE'),
ROW('12', '2', 'FALSE')
         ) source_data
WHERE NOT EXISTS (SELECT NULL FROM PATIENT_REGISTRATION);


INSERT INTO MEDICATION (NAME)
SELECT 'FOO_MED'
WHERE NOT EXISTS (SELECT ID
                  FROM MEDICATION
                  WHERE ID = '1');

INSERT INTO PRESCRIPTION (MEDICATION_ID, PATIENT_ID, PRACTITIONER_ID, BEGIN_TIME, DOSE_MG)
SELECT '1', '4', '2', {ts '2024-11-01 10:34:53'}, '15'
WHERE NOT EXISTS (SELECT PATIENT_ID
                  FROM PRESCRIPTION
                  WHERE PATIENT_ID = '4');
INSERT INTO PRESCRIPTIONSCHEDULEENTRY (DAY_STAGE, PRESCRIPTION_ID)
SELECT 'BEDTIME', '1'
WHERE NOT EXISTS (SELECT PRESCRIPTION_ID
                  FROM PRESCRIPTIONSCHEDULEENTRY
                  WHERE PRESCRIPTION_ID = '1'
                    AND DAY_STAGE = 'BEDTIME');

INSERT INTO DAILYEVALUATION (USERMODEL_ID, RECORD_DATE)
SELECT '4', {ts '2024-12-01'}
WHERE NOT EXISTS (SELECT USERMODEL_ID
                  FROM DAILYEVALUATION
                  WHERE USERMODEL_ID = '4');

INSERT INTO DOSE (DOSE_TIME, TAKEN, dailyevaluation_record_Date, dailyevaluation_userModel_id,
                  prescription_schedule_entry_id)
SELECT {ts '2024-12-01 10:34:53'}, 'TRUE', {ts '2024-12-01'}, '4', '1'
WHERE NOT EXISTS (SELECT ID
                  FROM DOSE
                  WHERE ID = '1');
INSERT INTO BLOODPRESSUREREADING (dailyevaluation_record_Date, dailyevaluation_userModel_id, systole, diastole,
                                  heart_rate, reading_time, day_stage)
SELECT {ts '2024-12-01'}, '4', '111', '77', '66', {ts '2024-12-01 10:34:53'}, 'WAKEUP'
WHERE NOT EXISTS (SELECT ID
                  FROM BLOODPRESSUREREADING
                  WHERE ID = '1');


-- all password in plaintext are 'abc'