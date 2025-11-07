Description

This API powers a candidate matching system where users can manage candidates and their technical skills. It supports creating, updating, and linking candidates and skills, and enriches candidate skill data by calling an external Skill Stats API. The system uses JWT authentication, exposes REST endpoints, and returns all data in JSON format.


Tech

The api is build using the following: Java – version 17 Corretto Maven pom file and dependency library Javalin server configuration PostgreSQL database handled by hibernate using JPA JWT-based security with password hashing using bcrypt/jcryp See the pom.xml for needed dependencies

Setup instructions

Start by cloning the project. The securitysystem demands that you give a user owner rights on the database before you will be able to make tables and put fields in to the tables. Currently the user is set to: username : dev2 and password ax22. In setDevProperties in the Hibernate configuration you can/should change that to your own values for safety reasons. They are only needed for using a database while developing or running the program locally on Localhost. If you deploy the project you need to set these values and database name in the system variables instead along other configurations. Its also important that you add your class annotations for database entities I the getAnnotationConfiguration inside Hibernate configuration else JPA won’t be able to recognize where to look for your entities so it can automate database services based on your initial setup for the database and the services run when users and administrators manage and use the trip service. Also make sure that you adjust hibernates properties regards the use of the database. Make sure you configure create the first time around in the property type you currently run so you get a fresh database. After that you need to change it to update, so you don’t lose your data next time you start the application. The interface is set up to receive and deliver data in Json string format. But the security part demands that you always need a valid registered username and password to access anything beside the register and login system. Any other endpoint demands a user with the correct Role for access. Se user types needed roles and existing endpoints below.

Security

Only /api/v1/auth/login and /api/v1/auth/register are public; every other endpoint requires a valid token and the correct role (USER / ADMIN). How it works Client logs in -> receives a JWT. The server validates the token, and verifies that the user’s role matches the endpoint requirement or reject if they don´t match. Configuration Local development: use config.properties file to make SECRET_KEY= to exactly 32 characters

Request Body and Response Formats

Every endpoint have af the following base URL:  