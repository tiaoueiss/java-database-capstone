This Spring Boot application uses both MVC and REST controllers. Thymeleaf templates are used for the Admin and Doctor dashboards, while REST APIs serve all other modules. 
The application interacts with two databases—MySQL (for patient, doctor, appointment, and admin data) and MongoDB (for prescriptions). 
All controllers route requests through a common service layer, which in turn delegates to the appropriate repositories.
MySQL uses JPA entities while MongoDB uses document models.

1. User accesses the system through the frontend interface (HTML/CSS/JavaScript pages such as Login, Admin Dashboard, Appointment Management, or Patient Portal).
2. The request is sent to the backend application, where it is routed to the appropriate Spring Boot Controller (for example, LoginController, AppointmentController, DoctorController, or PatientController).
3. The controller validates the request and forwards it to the Service Layer, which contains the business logic for operations such as scheduling appointments, retrieving patient records, or managing users.
4. The service layer communicates with the Repository/Data Access Layer, which interacts with the databases using Spring Data JPA repositories for MySQL and MongoDB repositories for document data.
5. Structured relational data (such as doctors, patients, admins, and appointments) is stored and retrieved from the MySQL database, while flexible data like prescriptions and medical notes is stored in MongoDB.
6. The repository layer returns the requested data to the service layer, which processes the results and prepares a response object for the controller.
7. The controller sends the final response back to the frontend, where the user interface updates the page and displays the requested information to the user.
