# MedTracker
## Description

Web app to track and visualize the effects of medication prescriptions.\
This project also exposes a rest api that can be reached with the react app found in the medtracker-react-frontend repo
https://github.com/OBrien-Cathal/medtracker-react-frontend

Technologies used:
- Spring Boot
- DB: H2
- Front-end: Initially static pages created using Thymeleaf templating engine, this has now been removed. 
- File Upload processing: Apache POI XSSF

## Initial setup
On startup the project will create a folder in the users home directory H2DBs, H2 database files will be stored here.\
A webserver will be started on port 3100

## How to use
A rest api is exposed on port 3100

## Authentication


Username and password posted via http to localhost:3100/api/v1/auth/signin will provide a JWT that can be provided 
as a bearer token in the header of subsequent authenticated requests.

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
 

### Backend Testing
- Repository Tests for basic queries
- Service tests
- Controller tests

