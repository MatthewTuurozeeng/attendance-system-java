# Attendance Management System (OOP Final Project)

An object-oriented, JavaFX-based attendance management system built as a final project for the OOP course. The app focuses on faculty workflows: sign up, log in, add students to a course, and track attendance over time. Student and faculty data are persisted locally in CSV files for simplicity and portability.

## What this project does

- **Role-based login** for faculty and students.
- **Faculty sign-up & login** with basic validation.
- **Student sign-up** with validation and duplicate checks.
- **Faculty dashboard** to add students, mark attendance, and track percentages.
- **Student dashboard** showing attendance %, course details, and history.
- **Attendance history logging** with per-course filtering.
- **Profile editing** for students (name + email).
- **Export attendance history** to CSV/PDF.
- **Lightweight local storage** (CSV) that works without a database.

## Core concepts demonstrated

- Object-oriented design (inheritance with `Person`, encapsulated domain models).
- JavaFX UI composition and event handling.
- File I/O with CSV persistence.
- Simple validation rules for inputs.

## Project structure

```
src/
	java/
		module-info.java
		com/example/attendance_management/
			LaunchPage.java          # JavaFX entry (login + navigation)
			SceneOne.java            # Main UI (role-based login + dashboards)
			FacultySignUpWindow.java # Faculty sign-up flow + validation
			Student.java             # Student model + CSV persistence
			Faculty.java             # Faculty model + CSV persistence
			Course.java              # Course model
			Attendance.java          # Attendance tracking utilities
			ManagementDriver.java    # Console-only fallback runner
	resources/
		styles.css                 # JavaFX styles
		com/example/attendance_management/hello-view.fxml
		images/
```

## Data storage

The application stores data in CSV files in the project root:

- `students.csv`
	- Header: `Name,ID,Email,Unique-Code,Attendance Status,Attendance(in %)`
- `facultyDetails.csv`
	- Header: `ID,Name,Email,Course Teaching`
- `attendance_history.csv`
	- Header: `StudentID,Timestamp,Present,AttendancePercentage,Course`
- `exports/`
	- Generated exports (CSV/PDF) for student attendance history

These files are created automatically if they don’t exist.

##  Prerequisites

- **Java 21** (project is compiled for Java 21)
- Maven (or use the Maven wrapper if it’s fully configured in your clone)

The `pom.xml` already includes JavaFX and all required UI dependencies.

##  How to run

### Option A — JavaFX UI (recommended)

The Maven JavaFX plugin is configured, so you can run the UI directly:

```bash
./mvnw clean javafx:run
# or
mvn clean javafx:run
```

The JavaFX entry point is already wired to `LaunchPage`.

### Option B — Console mode (fallback)

There is a terminal-based runner that can be used without JavaFX:

```bash
./mvnw -q -DskipTests package
# or
mvn -q -DskipTests package

java -cp target/classes com.example.attendance_management.ManagementDriver
```

## Tests

JUnit 5 and TestFX are configured, with unit and UI tests included.

```bash
./mvnw test
# or
mvn test
```

## Troubleshooting

- **JavaFX window doesn’t open:** Ensure Java 21 is installed and active.
- **CSV not updating:** Make sure the app has write access to the project root.
- **Login fails after sign-up:** Sign-up data is stored in `facultyDetails.csv` — check the file exists and contains the new record.

##  Notes

This project is intentionally lightweight and suitable for learning purposes. It can be extended with:

- A database (SQLite/PostgreSQL)
- Role-based access (students vs faculty)
- Exported attendance reports are saved under `exports/`.

---
