# Attendance Management System (OOP Final Project)

An object-oriented, JavaFX-based attendance management system built as a final project for the OOP course. The app focuses on faculty workflows: sign up, log in, add students to a course, and track attendance over time. Student and faculty data are persisted locally in CSV files for simplicity and portability.

## What this project does

- **Faculty sign-up & login** with basic validation.
- **Add students** to a faculty member’s course.
- **Mark attendance** using a simple checkbox-based UI.
- **Track attendance percentage** and persist updates to CSV files.
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
			SceneOne.java            # Main UI (faculty area + student table)
			FacultySignUpWindow.java # Sign-up flow + validation
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

The application stores data in two CSV files in the project root:

- `students.csv`
	- Header: `Name,ID,Email,Unique-Code,Attendance Status,Attendance(in %)`
- `facultyDetails.csv`
	- Header: `ID,Name,Email,Course Teaching`

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

By default, Maven runs `HelloApplication`. To launch the full UI (`LaunchPage`) instead, update the `mainClass` entry in `pom.xml` to:

```
com.example.attendance_management/com.example.attendance_management.LaunchPage
```

### Option B — Console mode (fallback)

There is a terminal-based runner that can be used without JavaFX:

```bash
./mvnw -q -DskipTests package
# or
mvn -q -DskipTests package

java -cp target/classes com.example.attendance_management.ManagementDriver
```

## Tests

JUnit 5 is configured in the build, though no tests are currently included.

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
- Exportable attendance reports (CSV/PDF)

---
