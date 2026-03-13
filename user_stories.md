# Smart Clinic Management System - User Stories

---

## Admin User Stories

### Title:
_As an Admin, I want to add new doctors and patients to the system, so that the clinic database remains up-to-date._

**Acceptance Criteria:**
1. Admin can create a new doctor account with name, specialty, and contact information.
2. Admin can create a new patient account with name, contact info, and health info.
3. The system validates required fields and prevents duplicate emails.

**Priority:** High  
**Story Points:** 5  
**Notes:**
- Should log creation actions for audit purposes.
- Handle email format validation.

---

### Title:
_As an Admin, I want to assign roles and permissions, so that users have appropriate access levels._

**Acceptance Criteria:**
1. Admin can assign roles: Doctor, Patient, or Admin.
2. Admin can modify permissions for specific system modules.
3. Changes are logged for audit and security purposes.

**Priority:** High  
**Story Points:** 4  
**Notes:**
- Include role-based access control (RBAC) enforcement.
- Avoid giving Admin rights to non-admin users by mistake.

---

## Patient User Stories

### Title:
_As a Patient, I want to register and create a profile, so that I can book appointments._

**Acceptance Criteria:**
1. Patient can sign up with name, email, password, and basic health info.
2. System validates input and prevents duplicate registrations.
3. Confirmation email is sent upon successful registration.

**Priority:** High  
**Story Points:** 3  
**Notes:**
- Include password strength validation.
- Handle email verification edge cases.

---

### Title:
_As a Patient, I want to book an appointment with a doctor, so that I can receive care at a convenient time._

**Acceptance Criteria:**
1. Patient can select a doctor and available time slot.
2. System confirms the booking and updates the doctor’s schedule.
3. Patient receives a confirmation notification.

**Priority:** High  
**Story Points:** 3  
**Notes:**
- Prevent double-booking for the same time slot.
- Allow rescheduling within clinic rules.

---

## Doctor User Stories

### Title:
_As a Doctor, I want to view my daily and weekly appointment schedule, so that I can plan my work._

**Acceptance Criteria:**
1. Doctor can see all upcoming appointments in a calendar view.
2. Doctor can filter appointments by date or patient.
3. System updates the schedule in real-time when patients book/cancel appointments.

**Priority:** High  
**Story Points:** 3  
**Notes:**
- Use calendar UI for better visualization.
- Include real-time notifications for changes.

---

### Title:
_As a Doctor, I want to add prescriptions for my patients, so that their treatment information is recorded accurately._

**Acceptance Criteria:**
1. Doctor can select a patient from their appointment list.
2. Doctor can add prescription details (medicine name, dosage, duration).
3. Prescription is stored in MongoDB and linked to the patient record.

**Priority:** Medium  
**Story Points:** 3  
**Notes:**
- Include optional notes for special instructions.
- Allow edits only before patient acknowledgment.
