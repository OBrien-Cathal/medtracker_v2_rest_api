# MedTracker
## Description

Web app to track and visualize the effects of medication prescriptions.

Technologies used:
- Spring Boot
- DB: H2
- Front-end: Static pages created using Thymeleaf templating engine
- File Upload processing: Apache POI XSSF

## Initial setup
On startup the project will create a folder in the users home directory H2DBs, H2 database files will be stored here.\
A webserver will be started on port 3100

## How to use
After starting the application, in a browser go to localhost:3100.\
A homepage with login and registration links will be presented.

Unauthorized users can register an account. New accounts have the Basic/Patient role by default.

Several types of user exist in the system.
- Basic/Patient
  - (unimplemented) Request registration with one or more practitioners
  - Bulk upload data from Excel files
  - View their prescriptions
  - Request a role upgrade to "Practitioner"
    - this request must be reviewed and approved by a user with the "Admin" role
  - View visualizations of their uploaded data, "Data Upload" page
- Practitioner
  - View a list of all "Patient" users
    - (unimplemented) filter by patients registered with the practitioner
  - (unimplemented) view patient data, including prescriptions from other practitioners
  - (unimplemented) prescribe medications to their registered patients
  - (unimplemented) add new medications to the system
  - (unimplemented) view all medications
- Admin
  - View all users 
  - Approved role upgrades
  - Downgrade roles



### Pages
After registering and logging in, the users can use 
#### Patient
###### Uploads
Navigating to the "Upload" page will allow you to upload different types of .xlsx file.\
Example files for the bulk uploads can be found
- Doses
  - ```
    src/main/resources/InitialDataFiles/doses.xlsx
    ```
- Blood Pressure
  - ``` 
    src/main/resources/InitialDataFiles/bloodPressureReadings.xlsx
    ```
###### Data Overview
Navigating to the "Data Overview" page will display some visualizations of the uploaded data
- Medication Dose
- Blood Pressure
  - Systole
  - Diastole

###### Prescriptions
Navigating to the "Prescriptions" page will display a list of prescriptions for the patient

#### Practitioner
###### Patients
Navigating to the "Patients" page will display a list of all patients in the system

#### Admin
###### Admin: User Management
Navigating to the "ADMIN: User Management" page will:
- display a list of all users in the system
- display a list of all practitioner role requests and their current status
  - unchecking the approved checkbox will downgrade the users role and delete the role request from the system, 
allowing the user to request a role upgrade again in the future.

## Developer notes
### Ongoing Development
The majority of currently unimplemented features mentioned in this document are supported by the current object model,
except patient registration with a practitioner.

### DB Filling
On startup of the application two sources of initial data may be loaded
- On an empty DB the data.sql file will fill some basic bootstrapping data
- If there are less than two medications in the DB (this rule is totally arbitrary), then extra data will be loaded from the files located
  - ``` 
      src/main/resources/InitialDataFiles
      ```
    
### Object Model Diagram
```
requirements/objectModel.graphml
```
### Client Testing
Selenium project file location
```
testing/MedTracker.side
```

### Backend Testing
- Repository Tests for basic queries