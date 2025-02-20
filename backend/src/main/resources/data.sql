INSERT INTO USERMODEL (username, password, role)
SELECT *
FROM (VALUES ('admin@medtracker.com', '$2a$10$WSU4n.NhUE7g1lwMAeTT9OXAaGJG2s.4UkhYIYuIcT0qn0AxNV8NO',
              cast('ADMIN' as USER_ROLE)),
             ('doc1@medtracker.com', '$2a$10$WSU4n.NhUE7g1lwMAeTT9OXAaGJG2s.4UkhYIYuIcT0qn0AxNV8NO',
              cast('PRACTITIONER' as USER_ROLE)),
             ('patient1@medtracker.com', '$2a$10$WSU4n.NhUE7g1lwMAeTT9OXAaGJG2s.4UkhYIYuIcT0qn0AxNV8NO',
              cast('PATIENT' as USER_ROLE)),
             ('patient2@medtracker.com', '$2a$10$WSU4n.NhUE7g1lwMAeTT9OXAaGJG2s.4UkhYIYuIcT0qn0AxNV8NO',
              cast('PATIENT' as USER_ROLE)),
             ('user1@medtracker.com', '$2a$10$WSU4n.NhUE7g1lwMAeTT9OXAaGJG2s.4UkhYIYuIcT0qn0AxNV8NO',
              cast('USER' as USER_ROLE)),
             ('user2@medtracker.com', '$2a$10$WSU4n.NhUE7g1lwMAeTT9OXAaGJG2s.4UkhYIYuIcT0qn0AxNV8NO',
              cast('USER' as USER_ROLE)),
             ('user3@medtracker.com', '$2a$10$WSU4n.NhUE7g1lwMAeTT9OXAaGJG2s.4UkhYIYuIcT0qn0AxNV8NO',
              cast('USER' as USER_ROLE)),
             ('user4@medtracker.com', '$2a$10$WSU4n.NhUE7g1lwMAeTT9OXAaGJG2s.4UkhYIYuIcT0qn0AxNV8NO',
              cast('USER' as USER_ROLE)),
             ('user5@medtracker.com', '$2a$10$WSU4n.NhUE7g1lwMAeTT9OXAaGJG2s.4UkhYIYuIcT0qn0AxNV8NO',
              cast('USER' as USER_ROLE)),
             ('user6@medtracker.com', '$2a$10$WSU4n.NhUE7g1lwMAeTT9OXAaGJG2s.4UkhYIYuIcT0qn0AxNV8NO',
              cast('USER' as USER_ROLE)),
             ('user7@medtracker.com', '$2a$10$WSU4n.NhUE7g1lwMAeTT9OXAaGJG2s.4UkhYIYuIcT0qn0AxNV8NO',
              cast('USER' as USER_ROLE)),
             ('user8@medtracker.com', '$2a$10$WSU4n.NhUE7g1lwMAeTT9OXAaGJG2s.4UkhYIYuIcT0qn0AxNV8NO',
              cast('USER' as USER_ROLE)),
             ('doc2@medtracker.com', '$2a$10$WSU4n.NhUE7g1lwMAeTT9OXAaGJG2s.4UkhYIYuIcT0qn0AxNV8NO',
              cast('PRACTITIONER' as USER_ROLE)),
             ('doc3@medtracker.com', '$2a$10$WSU4n.NhUE7g1lwMAeTT9OXAaGJG2s.4UkhYIYuIcT0qn0AxNV8NO',
              cast('PRACTITIONER' as USER_ROLE))) source_data
WHERE NOT EXISTS (SELECT NULL FROM USERMODEL);



INSERT INTO ACCOUNT_REGISTRATION (USERMODEL_ID, CONFIRMED, REGISTRATION_ID, REGISTRATION_TIME, CONFIRMATION_TIME)
SELECT *
FROM (VALUES (1, (cast(1 as boolean)), (cast('4fd1b3de-3f98-4cc1-bee4-13948f6b7b27' as uuid)),
              {ts '2024-12-01 10:34:53'},
              {ts '2024-12-01 10:34:53'}),
             (2, (cast(1 as boolean)), (cast('4fd1b3de-3f98-4cc1-bee4-13948f6b7b27' as uuid)),
              {ts '2024-12-01 10:34:53'},
              {ts '2024-12-01 10:34:53'}),
             (3, (cast(1 as boolean)), (cast('4fd1b3de-3f98-4cc1-bee4-13948f6b7b27' as uuid)),
              {ts '2024-12-01 10:34:53'},
              {ts '2024-12-01 10:34:53'}),
             (4, (cast(1 as boolean)), (cast('4fd1b3de-3f98-4cc1-bee4-13948f6b7b27' as uuid)),
              {ts '2024-12-01 10:34:53'},
              {ts '2024-12-01 10:34:53'}),
             (5, (cast(1 as boolean)), (cast('4fd1b3de-3f98-4cc1-bee4-13948f6b7b27' as uuid)),
              {ts '2024-12-01 10:34:53'},
              {ts '2024-12-01 10:34:53'}),
             (6, (cast(1 as boolean)), (cast('4fd1b3de-3f98-4cc1-bee4-13948f6b7b27' as uuid)),
              {ts '2024-12-01 10:34:53'},
              {ts '2024-12-01 10:34:53'}),
             (7, (cast(0 as boolean)), (cast('4fd1b3de-3f98-4cc1-bee4-13948f6b7b27' as uuid)),
              {ts '2024-12-01 10:34:53'},
              {ts '2024-12-01 10:34:53'}),
             (8, (cast(0 as boolean)), (cast('4fd1b3de-3f98-4cc1-bee4-13948f6b7b27' as uuid)),
              {ts '2024-12-01 10:34:53'},
              {ts '2024-12-01 10:34:53'}),
             (9, (cast(1 as boolean)), (cast('4fd1b3de-3f98-4cc1-bee4-13948f6b7b27' as uuid)),
              {ts '2024-12-01 10:34:53'},
              {ts '2024-12-01 10:34:53'}),
             (10, (cast(1 as boolean)), (cast('4fd1b3de-3f98-4cc1-bee4-13948f6b7b27' as uuid)),
              {ts '2024-12-01 10:34:53'},
              {ts '2024-12-01 10:34:53'}),
             (11, (cast(1 as boolean)), (cast('4fd1b3de-3f98-4cc1-bee4-13948f6b7b27' as uuid)),
              {ts '2024-12-01 10:34:53'},
              {ts '2024-12-01 10:34:53'}),
             (12, (cast(1 as boolean)), (cast('4fd1b3de-3f98-4cc1-bee4-13948f6b7b27' as uuid)),
              {ts '2024-12-01 10:34:53'},
              {ts '2024-12-01 10:34:53'}),
             (13, (cast(1 as boolean)), (cast('4fd1b3de-3f98-4cc1-bee4-13948f6b7b27' as uuid)),
              {ts '2024-12-01 10:34:53'},
              {ts '2024-12-01 10:34:53'}),
             (14, (cast(1 as boolean)), (cast('4fd1b3de-3f98-4cc1-bee4-13948f6b7b27' as uuid)),
              {ts '2024-12-01 10:34:53'},
              {ts '2024-12-01 10:34:53'})) source_data
WHERE NOT EXISTS (SELECT NULL FROM ACCOUNT_REGISTRATION);

INSERT INTO ACCOUNT_DETAILS (FIRST_NAME, SURNAME, USERMODEL_ID, ID)
SELECT *
FROM (VALUES (('FIRST', 'SECOND', 1, 1),
              ('FIRST', 'SECOND', 2, 2),
              ('FIRST', 'SECOND', 3, 3),
              ('FIRST', 'SECOND', 4, 4),
              ('FIRST', 'SECOND', 5, 5),
              ('FIRST', 'SECOND', 6, 6),
              ('FIRST', 'SECOND', 7, 7),
              ('FIRST', 'SECOND', 8, 8),
              ('FIRST', 'SECOND', 9, 9),
              ('FIRST', 'SECOND', 10, 10),
              ('FIRST', 'SECOND', 11, 11),
              ('FIRST', 'SECOND', 12, 12),
              ('FIRST', 'SECOND', 12, 13),
              ('FIRST', 'SECOND', 14, 14))) source_data
WHERE NOT EXISTS (SELECT NULL FROM ACCOUNT_DETAILS);



INSERT INTO ROLECHANGE (NEW_ROLE, OLD_ROLE, USERMODEL_ID, REQUEST_TIME, APPROVED_BY_ID, APPROVAL_TIME)
SELECT *
FROM (VALUES (cast('ADMIN' as USER_ROLE), cast('USER' as USER_ROLE), 1, {ts '2024-12-01 10:34:53'}, 1,
              {ts '2024-12-01 10:34:53'}),
             (cast('PRACTITIONER' as USER_ROLE), cast('USER' as USER_ROLE), 2, {ts '2024-12-01 10:34:53'}, 1,
              {ts '2024-12-01 10:34:53'}),
             (cast('PATIENT' as USER_ROLE), cast('USER' as USER_ROLE), 3, {ts '2024-12-01 10:34:53'}, 2,
              {ts '2024-12-01 10:34:53'}),
             (cast('PATIENT' as USER_ROLE), cast('USER' as USER_ROLE), 4, {ts '2024-12-01 10:34:53'}, 2,
              {ts '2024-12-01 10:34:53'}),

             (cast('ADMIN' as USER_ROLE), cast('USER' as USER_ROLE), 8, {ts '2024-12-01 10:34:53'}, NULL, NULL),
             (cast('PRACTITIONER' as USER_ROLE), cast('USER' as USER_ROLE), 8, {ts '2024-12-01 10:34:53'}, NULL, NULL),

             (cast('PRACTITIONER' as USER_ROLE), cast('USER' as USER_ROLE), 9, {ts '2024-12-01 10:34:53'}, NULL, NULL),

             (cast('ADMIN' as USER_ROLE), cast('USER' as USER_ROLE), 10, {ts '2024-12-01 10:34:53'}, NULL, NULL),

             (cast('PRACTITIONER' as USER_ROLE), cast('USER' as USER_ROLE), 11, {ts '2024-12-01 10:34:53'}, 1,
              {ts '2024-12-01 10:34:53'}),
             (cast('PRACTITIONER' as USER_ROLE), cast('USER' as USER_ROLE), 12, {ts '2024-12-01 10:34:53'}, 1,
              {ts '2024-12-01 10:34:53'})) source_data
WHERE NOT EXISTS (SELECT NULL FROM ROLECHANGE);

INSERT INTO PATIENT_REGISTRATION (USERMODEL_ID, PRACTITIONER_ID, REGISTERED)
SELECT *
FROM (VALUES (3, 2, cast(1 as boolean)),
             (4, 2, cast(1 as boolean)),
             (11, 2, cast(0 as boolean)),
             (12, 2, cast(0 as boolean))) source_data
WHERE NOT EXISTS (SELECT NULL FROM PATIENT_REGISTRATION);


INSERT INTO MEDICATION (NAME)
SELECT 'FOO_MED'
WHERE NOT EXISTS (SELECT ID
                  FROM MEDICATION
                  WHERE ID = '1');

INSERT INTO PRESCRIPTION (MEDICATION_ID, PATIENT_ID, PRACTITIONER_ID, BEGIN_TIME, END_TIME, DOSE_MG)
SELECT '1', '5', '2', {ts '2024-12-01 10:34:53'}, {ts '2024-12-01 10:34:53'}, '15'
WHERE NOT EXISTS (SELECT PATIENT_ID
                  FROM PRESCRIPTION
                  WHERE PATIENT_ID = '4');
INSERT INTO PRESCRIPTIONSCHEDULEENTRY (DAY_STAGE, PRESCRIPTION_ID)
SELECT cast('BEDTIME' as DAYSTAGE), '1'
WHERE NOT EXISTS (SELECT PRESCRIPTION_ID
                  FROM PRESCRIPTIONSCHEDULEENTRY
                  WHERE PRESCRIPTION_ID = '1'
                    AND DAY_STAGE = 'BEDTIME');

INSERT INTO DAILYEVALUATION (USERMODEL_ID, RECORD_DATE)
SELECT '5', {ts '2024-12-01'}
WHERE NOT EXISTS (SELECT USERMODEL_ID
                  FROM DAILYEVALUATION
                  WHERE USERMODEL_ID = '5');

INSERT INTO DOSE (DOSE_TIME, TAKEN, dailyevaluation_record_Date, dailyevaluation_userModel_id,
                  prescription_schedule_entry_id)
SELECT {ts '2024-12-01 10:34:53'}, '1', {ts '2024-12-01'}, '5', '1'
WHERE NOT EXISTS (SELECT ID
                  FROM DOSE
                  WHERE ID = '1');
INSERT INTO BLOODPRESSUREREADING (dailyevaluation_record_Date, dailyevaluation_userModel_id, systole, diastole,
                                  heart_rate, reading_time, day_stage)
SELECT {ts '2024-12-01'}, '5', '111', '77', '66', {ts '2024-12-01 10:34:53'}, 'WAKEUP'
WHERE NOT EXISTS (SELECT ID
                  FROM BLOODPRESSUREREADING
                  WHERE ID = '1');


-- all password in plaintext are 'abc'