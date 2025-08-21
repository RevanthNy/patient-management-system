# Patient Management System
This is a full-stack application for managing patient records. It includes a backend API (Java/Spring Boot), a frontend user interface (React), and a database (PostgreSQL) with a graphical user interface (Adminer), all running together in Docker.

## Prerequisites
Before you begin, you need to have the following installed on your computer:

```
Docker
Docker Compose
```

No other software (like Java, Node.js, or PostgreSQL) is needed on your machine. Everything runs inside Docker containers.

## One-Command Setup
Follow these simple steps to get the entire application up and running.

1. Open a Terminal
Open your terminal or command prompt.

2. Navigate to the Project Folder
Go into the ```patient-management-system folder``` where this ```README.md``` file is located.

3. Run the Command
Execute the following command. This will build the container images for the frontend and backend, and then start all the services. The first time you run this, it will take a few minutes.

```
docker-compose up --build
```

## How to Access the Application
Once the command finishes and the logs are running, you can access the different parts of the system:

1. Main User Interface: Open your web browser and go to: http://localhost:5173
2. Database GUI (Adminer): Open a new browser tab and go to: http://localhost:8081
3. Backend Service: Can be accessed by checking logs and making custom requests using Postman at http://localhost:8080

Use the following credentials to log in:

```
System: PostgreSQL
Server: patient-db
Username: admin
Password: password
Database: patient_service
```

## How to Stop the Application
To stop all the running services, go to your terminal window where the application is running and press ```Ctrl + C```.
