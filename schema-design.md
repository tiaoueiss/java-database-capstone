# Smart Clinic Management System - Database Schema Design

---

## MySQL Database Design

Structured, relational data for core operations of the clinic.

### Table: patients
- id: INT, Primary Key, AUTO_INCREMENT
- first_name: VARCHAR(50), NOT NULL
- last_name: VARCHAR(50), NOT NULL
- email: VARCHAR(100), NOT NULL, UNIQUE
- phone: VARCHAR(20), NOT NULL
- date_of_birth: DATE, NOT NULL
- created_at: DATETIME, NOT NULL, DEFAULT CURRENT_TIMESTAMP
- updated_at: DATETIME, DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
- **Comment:** Stores all patient information. Email is unique to prevent duplicates.

### Table: doctors
- id: INT, Primary Key, AUTO_INCREMENT
- first_name: VARCHAR(50), NOT NULL
- last_name: VARCHAR(50), NOT NULL
- email: VARCHAR(100), NOT NULL, UNIQUE
- specialty: VARCHAR(100), NOT NULL
- phone: VARCHAR(20), NOT NULL
- created_at: DATETIME, NOT NULL, DEFAULT CURRENT_TIMESTAMP
- updated_at: DATETIME, DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
- **Comment:** Each doctor has a specialty. Email is unique. Used in appointments FK.

### Table: appointments
- id: INT, Primary Key, AUTO_INCREMENT
- doctor_id: INT, NOT NULL, Foreign Key → doctors(id)
- patient_id: INT, NOT NULL, Foreign Key → patients(id)
- appointment_time: DATETIME, NOT NULL
- status: ENUM('Scheduled','Completed','Cancelled'), DEFAULT 'Scheduled'
- created_at: DATETIME, NOT NULL, DEFAULT CURRENT_TIMESTAMP
- updated_at: DATETIME, DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
- **Comment:** Appointment links patient and doctor. Status tracks progress.  
- **Business Logic Note:** Avoid overlapping appointments for the same doctor.

### Table: admin
- id: INT, Primary Key, AUTO_INCREMENT
- username: VARCHAR(50), NOT NULL, UNIQUE
- email: VARCHAR(100), NOT NULL, UNIQUE
- password_hash: VARCHAR(255), NOT NULL
- created_at: DATETIME, NOT NULL, DEFAULT CURRENT_TIMESTAMP
- updated_at: DATETIME, DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
- **Comment:** Admin accounts manage roles, users, and system-wide data.

---

## MongoDB Collection Design

Flexible document-based data for prescriptions and other metadata.

### Collection: prescriptions
```json
{
  "_id": "ObjectId('64abc123456')",
  "patientId": 101,
  "patientName": "John Smith",
  "appointmentId": 51,
  "medications": [
    {
      "name": "Paracetamol",
      "dosage": "500mg",
      "frequency": "every 6 hours",
      "duration_days": 5
    },
    {
      "name": "Amoxicillin",
      "dosage": "250mg",
      "frequency": "every 8 hours",
      "duration_days": 7
    }
  ],
  "doctorNotes": "Patient should rest and drink plenty of fluids.",
  "refillCount": 2,
  "pharmacy": {
    "name": "Walgreens SF",
    "location": "Market Street"
  },
  "tags": ["pain-relief", "antibiotic"],
  "createdAt": "2026-03-13T10:30:00Z",
  "updatedAt": "2026-03-13T10:30:00Z"
}
