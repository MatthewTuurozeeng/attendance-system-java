package com.example.attendance_management;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashSet;
import java.util.Set;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
public class SceneOne {
    static TextField em, ID;
    Stage window;
    public static Scene sn;
    Button login, signUp;
    private static TableView<Student> studentTable;
    private static Stage win;
    private static final ObservableList<Student> studentList = FXCollections.observableArrayList(); // ObservableList for filtered students
    private ToggleGroup roleGroup;

    public void launchPage() throws Exception {
        launchPage(new Stage());
    }

    public void launchPage(Stage stage) throws Exception {
        window = stage;
        window.setTitle("Student Attendance Management");

        // Create a Label for the welcome message
        Label welcomeMessage = new Label("Welcome to Edu-Track's Attendance Management System!");
        welcomeMessage.getStyleClass().add("welcome-message"); // Add custom style

        // Create user areas
        GridPane gp = new GridPane();
        gp.setPadding(new Insets(30, 30, 30, 30));  // Increased padding for better spacing
        gp.setVgap(15);  // Increased vertical gap
        gp.setHgap(15);  // Increased horizontal gap
        gp.setAlignment(Pos.CENTER);  // Center-align the content

        // Role selection
        roleGroup = new ToggleGroup();
        RadioButton facultyRole = new RadioButton("Faculty");
        facultyRole.setToggleGroup(roleGroup);
        facultyRole.setSelected(true);
        RadioButton studentRole = new RadioButton("Student");
        studentRole.setToggleGroup(roleGroup);
        HBox roleBox = new HBox(15, new Label("Login as:"), facultyRole, studentRole);
        roleBox.setAlignment(Pos.CENTER_LEFT);
        GridPane.setConstraints(roleBox, 1, 0);

        // Add email and password fields
        em = new TextField();
        em.setPromptText("Enter Ashesi email: ");
    em.setId("emailField");
        Label emailLabel = new Label("Email:");
        emailLabel.getStyleClass().addAll("label-text", "bold-text");
        GridPane.setConstraints(emailLabel, 0, 1);
        GridPane.setConstraints(em, 1, 1);

        ID = new TextField();
        ID.setPromptText("Enter your Ashesi ID: ");
    ID.setId("idField");
        Label passwordLabel = new Label("Password:");
        passwordLabel.getStyleClass().addAll("label-text", "bold-text");
        GridPane.setConstraints(passwordLabel, 0, 2);
        GridPane.setConstraints(ID, 1, 2);

    // Create login and sign up buttons
        login = new Button("  Log In  ");
        signUp = new Button("  Sign Up  ");
        FacultySignUpWindow sn1 = new FacultySignUpWindow();
    StudentSignUpWindow studentSignUpWindow = new StudentSignUpWindow();
        signUp.getStyleClass().add("button");
        signUp.setId("signUpButton");
        signUp.setOnAction(e -> sn1.storeSignUpDetail());

        login.getStyleClass().add("button");
        login.setId("loginButton");
        login.setOnAction(e -> {
            RadioButton selected = (RadioButton) roleGroup.getSelectedToggle();
            String role = selected == null ? "Faculty" : selected.getText();
            if ("Student".equalsIgnoreCase(role)) {
                Student student = findStudent(em.getText(), ID.getText());
                if (student != null) {
                    showStudentDashboard(student);
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING,
                            "Oops, invalid entries. Enter a registered student email and ID.");
                    alert.showAndWait();
                }
            } else {
                if (isAuthorised(em, ID)) {
                    showFacultyArea();
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING,
                            "Oops, invalid entries. Enter an Ashesi email and a faculty ID ");
                    alert.showAndWait();
                }
            }
        });

    // Add the components to the GridPane
        GridPane.setConstraints(login, 1, 3);
    Label msg = new Label("Faculty? Sign up below to manage your class.");
        GridPane.setConstraints(msg, 1, 4);
        GridPane.setConstraints(signUp, 1, 5);
    Button studentSignUp = new Button("Student Sign Up");
    studentSignUp.getStyleClass().add("button");
    studentSignUp.setOnAction(e -> studentSignUpWindow.show());
    GridPane.setConstraints(studentSignUp, 1, 7);
    Label studentMsg = new Label("Student? Register your details below.");
    GridPane.setConstraints(studentMsg, 1, 6);
    gp.getChildren().addAll(roleBox, emailLabel, em, passwordLabel, ID, login, msg, signUp, studentMsg, studentSignUp);

        // Create a VBox to contain the welcome message and the form
        VBox vp = new VBox();
        vp.setPadding(new Insets(30));  // Padding around the VBox
        vp.setSpacing(20);  // Spacing between elements in the VBox
        vp.setAlignment(Pos.CENTER);  // Center-align the content
        vp.getStyleClass().add("background");
        vp.getChildren().addAll(welcomeMessage, gp);

        // Create the scene and show the window
        sn = new Scene(vp, 1400, 800);
        sn.getStylesheets().add("file:src/main/resources/styles.css");
        window.setScene(sn);
        window.show();
    }

    // Validating login details
    public boolean isAuthorised(TextField email, TextField id) {
        String em = email.getText();
        String pass = id.getText();
        return FacultySignUpWindow.valInCSV(em) && FacultySignUpWindow.valInCSV(pass);
    }

    public void showFacultyArea() {
        win = new Stage();
        win.initModality(Modality.APPLICATION_MODAL);
        win.setTitle("STUDENTS TAKING THE COURSE - COURSE NAME");

        // Pre-populate student list with available students, filtering by faculty ID
        populateTable(ID.getText());

        // Mark button
        Button markAttendance = new Button("Mark Attendance");
        markAttendance.getStyleClass().add("button");
    markAttendance.setId("markAttendanceButton");

        // Button to add a student
        Button addButton = new Button("Add Student");
        addButton.getStyleClass().add("button");
    addButton.setId("addStudentButton");

        // Table to display student details
        studentTable = new TableView<>();
        studentTable.setItems(studentList);
        studentTable.getStyleClass().add("table-view-background");
    studentTable.setId("studentTable");

        // Create columns for the TableView
        TableColumn<Student, String> nColumn = new TableColumn<>("Name");
        nColumn.setMinWidth(200);
        nColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getName()));

        TableColumn<Student, String> idColumn = new TableColumn<>("ID");
        idColumn.setMinWidth(100);
        idColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getStudentId()));

        TableColumn<Student, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setMinWidth(250);
        emailColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getEmail()));

        TableColumn<Student, String> uniqueId = new TableColumn<>("Unique-Code");
        uniqueId.setMinWidth(250);
        uniqueId.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getUniqueCode()));

        TableColumn<Student, Boolean> attColumn = new TableColumn<>("Status");
        attColumn.setMinWidth(100);
        attColumn.setCellValueFactory(cellData -> cellData.getValue().attendanceProperty());
        attColumn.setCellFactory(CheckBoxTableCell.forTableColumn(attColumn));

        TableColumn<Student, Integer> attendancePercentageColumn = new TableColumn<>("Attendance %");
        attendancePercentageColumn.setMinWidth(120);
        attendancePercentageColumn.setCellValueFactory(cellData -> cellData.getValue().attendancePercentageProperty().asObject());

        // Add columns to the table
        studentTable.getColumns().addAll(nColumn, idColumn, emailColumn, uniqueId, attColumn, attendancePercentageColumn);
        studentTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        studentTable.refresh();
        studentTable.setEditable(true);

        // Add the Add Student button functionality
        addButton.setOnAction(e -> showAddStudentPopup());

        // Adding space before the button
        Region spacer = new Region();
        spacer.setPrefHeight(65);

        Region sp = new Region();
        sp.setPrefHeight(30);

        // Layout
        VBox vb = new VBox();
        vb.setPadding(new Insets(50, 50, 50, 50));
        vb.setVgrow(studentTable, Priority.ALWAYS);
        vb.getChildren().addAll(studentTable, spacer, addButton, sp, markAttendance);

        // Mark Attendance Action
        markAttendance.setOnAction(e -> {
            // Iterate over each student and update attendance
            for (Student student : studentList) {
                if (!student.attendanceProperty().get()) {
                    int currentAttendance = student.attendancePercentageProperty().get();
                    student.attendancePercentageProperty().set(Math.max(0, currentAttendance - 2)); // Decrease by 2%, but not below 0%
                }
                logAttendanceEntry(student);
                Student.saveStudentToCSV(student);  // Save the updated student data to CSV
            }

            // After attendance is marked, reset all checkboxes to empty
            for (Student student : studentList) {
                student.attendanceProperty().set(false); // Uncheck all checkboxes
            }

            // Show an alert to confirm attendance marking
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Attendance marked successfully!");
            alert.showAndWait();
        });

        // Scene
        Scene sn = new Scene(vb, 1400, 800);
        win.setScene(sn);
        win.showAndWait();
    }

    private void showStudentDashboard(Student student) {
        Stage studentStage = new Stage();
        studentStage.initModality(Modality.APPLICATION_MODAL);
        studentStage.setTitle("Student Dashboard");

    FacultyMeta meta = findFacultyMeta(student.getUniqueCode());
    String courseDetails = meta.course;
        Label nameLabel = new Label("Name: " + student.getName());
        Label idLabel = new Label("Student ID: " + student.getStudentId());
        Label emailLabel = new Label("Email: " + student.getEmail());
        Label facultyLabel = new Label("Faculty ID: " + student.getUniqueCode());
    Label courseLabel = new Label("Course: " + courseDetails);
    Label lecturerLabel = new Label("Lecturer: " + meta.lecturerName);
    Label creditLabel = new Label("Course Credits: " + meta.courseCredit);
        Label attendanceLabel = new Label("Attendance Status: " + student.attendanceProperty().get());
        Label percentageLabel = new Label("Attendance %: " + student.attendancePercentageProperty().get() + "%");

    Label historyLabel = new Label("Attendance History");
    ListView<String> historyList = new ListView<>();
    List<AttendanceEntry> entries = loadAttendanceHistoryEntries(student.getStudentId());
    ChoiceBox<String> courseFilter = new ChoiceBox<>();
    courseFilter.getItems().addAll(buildCourseFilter(entries));
    courseFilter.getSelectionModel().selectFirst();
    historyList.setItems(FXCollections.observableArrayList(buildHistoryDisplay(entries, courseFilter.getValue())));
    courseFilter.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) ->
        historyList.setItems(FXCollections.observableArrayList(buildHistoryDisplay(entries, newVal))));
    historyList.setPrefHeight(180);

    Button editProfile = new Button("Edit Profile");
    editProfile.getStyleClass().add("button");
    editProfile.setOnAction(e -> showEditStudentProfile(student, nameLabel, emailLabel));

    Button exportCsv = new Button("Export CSV");
    exportCsv.getStyleClass().add("button");
    exportCsv.setOnAction(e -> exportHistoryCsv(student, entries));

    Button exportPdf = new Button("Export PDF");
    exportPdf.getStyleClass().add("button");
    exportPdf.setOnAction(e -> exportHistoryPdf(student, entries));

    HBox actionRow = new HBox(12, editProfile, exportCsv, exportPdf);

    VBox content = new VBox(12,
        nameLabel,
        idLabel,
        emailLabel,
        facultyLabel,
        courseLabel,
                lecturerLabel,
                creditLabel,
        attendanceLabel,
        percentageLabel,
        actionRow,
        new Label("Filter by course:"),
        courseFilter,
        historyLabel,
        historyList);
        content.setPadding(new Insets(25));

        Scene scene = new Scene(content, 520, 300);
        studentStage.setScene(scene);
        studentStage.showAndWait();
    }

    private Student findStudent(String email, String studentId) {
        if (email == null || studentId == null) {
            return null;
        }
        String trimmedEmail = email.trim();
        String trimmedId = studentId.trim();
        return Student.loadFromCSV().stream()
                .filter(student -> trimmedId.equals(student.getStudentId())
                        && trimmedEmail.equalsIgnoreCase(student.getEmail()))
                .findFirst()
                .orElse(null);
    }

    private void logAttendanceEntry(Student student) {
        String fileName = "attendance_history.csv";
        File file = new File(fileName);
        boolean newFile = !file.exists();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String timestamp = LocalDateTime.now().format(formatter);
    String course = findFacultyMeta(student.getUniqueCode()).course;

        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName, true))) {
            if (newFile) {
                writer.println("StudentID,Timestamp,Present,AttendancePercentage,Course");
            }
            writer.println(String.join(",",
                    student.getStudentId(),
                    timestamp,
                    Boolean.toString(student.attendanceProperty().get()),
                    Integer.toString(student.attendancePercentageProperty().get()),
                    course));
        } catch (IOException e) {
            System.out.println("Error writing attendance history: " + e.getMessage());
        }
    }

    private List<AttendanceEntry> loadAttendanceHistoryEntries(String studentId) {
        List<AttendanceEntry> history = new ArrayList<>();
        File file = new File("attendance_history.csv");
        if (!file.exists()) {
            return history;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4 && parts[0].equals(studentId)) {
                    String course = parts.length >= 5 ? parts[4] : "Unknown";
                    history.add(new AttendanceEntry(parts[1], parts[2], parts[3], course));
                }
            }
        } catch (IOException e) {
            return history;
        }
        return history;
    }

    private List<String> buildHistoryDisplay(List<AttendanceEntry> entries, String courseFilter) {
        List<String> display = new ArrayList<>();
        for (AttendanceEntry entry : entries) {
            if (courseFilter != null && !"All".equals(courseFilter) && !courseFilter.equals(entry.course)) {
                continue;
            }
            display.add(entry.timestamp + " | Present: " + entry.present + " | %: " + entry.percentage + " | " + entry.course);
        }
        if (display.isEmpty()) {
            display.add("No attendance history yet.");
        }
        return display;
    }

    private List<String> buildCourseFilter(List<AttendanceEntry> entries) {
        Set<String> courses = new LinkedHashSet<>();
        courses.add("All");
        for (AttendanceEntry entry : entries) {
            courses.add(entry.course);
        }
        return new ArrayList<>(courses);
    }

    private FacultyMeta findFacultyMeta(String facultyId) {
        File file = new File("facultyDetails.csv");
        if (!file.exists()) {
            return new FacultyMeta("Not available", "Not available", "Not available");
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4 && parts[0].equals(facultyId)) {
                    String lecturerName = parts[1];
                    String course = parts[3];
                    String credit = parts.length >= 5 ? parts[4] : "Not available";
                    return new FacultyMeta(course, lecturerName, credit);
                }
            }
        } catch (IOException e) {
            return new FacultyMeta("Not available", "Not available", "Not available");
        }
        return new FacultyMeta("Not available", "Not available", "Not available");
    }

    private static class FacultyMeta {
        private final String course;
        private final String lecturerName;
        private final String courseCredit;

        private FacultyMeta(String course, String lecturerName, String courseCredit) {
            this.course = course;
            this.lecturerName = lecturerName;
            this.courseCredit = courseCredit;
        }
    }

    private void showEditStudentProfile(Student student, Label nameLabel, Label emailLabel) {
        Stage editStage = new Stage();
        editStage.initModality(Modality.APPLICATION_MODAL);
        editStage.setTitle("Edit Profile");

        TextField nameField = new TextField(student.getName());
        TextField emailField = new TextField(student.getEmail());

        Button saveButton = new Button("Save");
        saveButton.getStyleClass().add("button");
        saveButton.setOnAction(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            if (!name.matches("[a-zA-Z ]+")) {
                showAlert("Invalid name", "Use letters and spaces only.");
                return;
            }
            if (!email.endsWith("@ashesi.edu.gh")) {
                showAlert("Invalid email", "Use your Ashesi email address.");
                return;
            }
            student.setName(name);
            student.setEmail(email);
            Student.saveStudentToCSV(student);
            nameLabel.setText("Name: " + name);
            emailLabel.setText("Email: " + email);
            editStage.close();
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.getStyleClass().add("button");
        cancelButton.setOnAction(e -> editStage.close());

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(10);
        grid.setHgap(10);
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(saveButton, 0, 2);
        grid.add(cancelButton, 1, 2);

        Scene scene = new Scene(grid, 420, 200);
        editStage.setScene(scene);
        editStage.showAndWait();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING, message);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private void exportHistoryCsv(Student student, List<AttendanceEntry> entries) {
        File exportDir = new File("exports");
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }
        File file = new File(exportDir, student.getStudentId() + "-attendance.csv");
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println("StudentID,Timestamp,Present,AttendancePercentage,Course");
            for (AttendanceEntry entry : entries) {
                writer.println(String.join(",",
                        student.getStudentId(),
                        entry.timestamp,
                        entry.present,
                        entry.percentage,
                        entry.course));
            }
            showAlert("Export complete", "Saved CSV to: " + file.getPath());
        } catch (IOException e) {
            showAlert("Export failed", "Unable to export CSV.");
        }
    }

    private void exportHistoryPdf(Student student, List<AttendanceEntry> entries) {
        File exportDir = new File("exports");
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }
        File file = new File(exportDir, student.getStudentId() + "-attendance.pdf");
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new java.io.FileOutputStream(file));
            document.open();
            document.add(new Paragraph("Attendance History"));
            document.add(new Paragraph("Student: " + student.getName() + " (" + student.getStudentId() + ")"));
            document.add(new Paragraph(" "));
            if (entries.isEmpty()) {
                document.add(new Paragraph("No attendance history yet."));
            } else {
                for (AttendanceEntry entry : entries) {
                    document.add(new Paragraph(entry.timestamp + " | Present: " + entry.present
                            + " | %: " + entry.percentage + " | " + entry.course));
                }
            }
            showAlert("Export complete", "Saved PDF to: " + file.getPath());
        } catch (Exception e) {
            showAlert("Export failed", "Unable to export PDF.");
        } finally {
            document.close();
        }
    }

    private static class AttendanceEntry {
        private final String timestamp;
        private final String present;
        private final String percentage;
        private final String course;

        private AttendanceEntry(String timestamp, String present, String percentage, String course) {
            this.timestamp = timestamp;
            this.present = present;
            this.percentage = percentage;
            this.course = course;
        }
    }

    // Method to populate ObservableList with CSV data
    public static void populateTable(String facultyID) {
        String fileName = "students.csv";
        // Read existing students from the CSV file, filtering by faculty ID
        File file = new File(fileName);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line = reader.readLine(); // Skip the header
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty()) {
                        continue; // Skip empty lines
                    }
                    String[] parts = line.split(",");
                    if (parts[3].equals(facultyID)) {  // Filter students by faculty ID (uniqueCode)
                        studentList.add(new Student(parts[0], parts[1], parts[2], parts[3]));
                    }
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    // Popup window to create a new student
    private static void showAddStudentPopup() {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Add New Student");

        TextField nameField = new TextField();
        nameField.setPromptText("Enter student name");
    nameField.setId("studentNameField");

        TextField idField = new TextField();
        idField.setPromptText("Enter student ID");
    idField.setId("studentIdField");

        TextField emailField = new TextField();
        emailField.setPromptText("Enter student email");
    emailField.setId("studentEmailField");

        TextField fIdField = new TextField();
        fIdField.setPromptText("Enter your valid faculty Id");
    fIdField.setId("facultyIdField");

        Button createButton = new Button("Create Student");
    createButton.setId("createStudentButton");
        createButton.setOnAction(e -> {
            String name = nameField.getText().trim();
            String id = idField.getText().trim();
            String email = emailField.getText().trim();
            String fId = fIdField.getText().trim();

            if (!name.isEmpty() && !id.isEmpty() && email.endsWith("@ashesi.edu.gh")) {
                Student newStudent = new Student(name, id, email, fId);
                Student.saveStudentToCSV(newStudent);
                studentList.add(newStudent);
                popup.close();
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Invalid input. Please fill out all fields correctly.");
                alert.showAndWait();
            }
        });

        Button cancelButton = new Button("Cancel");
    cancelButton.setId("cancelStudentButton");
        cancelButton.setOnAction(e -> popup.close());

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20, 20, 20, 20));
        vbox.getChildren().addAll(nameField, idField, emailField, fIdField, createButton, cancelButton);

        Scene scene = new Scene(vbox, 300, 250);
        popup.setScene(scene);
        popup.showAndWait();
    }

    private static class StudentSignUpWindow {
        private TextField nameField;
        private TextField studentIdField;
        private TextField emailField;
        private TextField facultyIdField;
        private Stage popUp;

        public void show() {
            popUp = new Stage();
            popUp.setTitle("Student Sign Up");

            GridPane grid = new GridPane();
            grid.setPadding(new Insets(20, 20, 20, 20));
            grid.setVgap(10);
            grid.setHgap(10);

            nameField = new TextField();
            nameField.setPromptText("Your full name");
            Label nameLabel = new Label("Name:");

            studentIdField = new TextField();
            studentIdField.setPromptText("Enter your student ID (8 digits)");
            Label idLabel = new Label("Student ID:");

            emailField = new TextField();
            emailField.setPromptText("Enter your Ashesi email");
            Label emailLabel = new Label("Email:");

            facultyIdField = new TextField();
            facultyIdField.setPromptText("Enter your faculty ID (6 digits)");
            Label facultyLabel = new Label("Faculty ID:");

            Button createButton = new Button("Create Student");
            createButton.getStyleClass().add("button");
            createButton.setOnAction(e -> createStudent());

            Button cancelButton = new Button("Cancel");
            cancelButton.getStyleClass().add("button");
            cancelButton.setOnAction(e -> popUp.close());

            grid.add(nameLabel, 0, 0);
            grid.add(nameField, 1, 0);
            grid.add(idLabel, 0, 1);
            grid.add(studentIdField, 1, 1);
            grid.add(emailLabel, 0, 2);
            grid.add(emailField, 1, 2);
            grid.add(facultyLabel, 0, 3);
            grid.add(facultyIdField, 1, 3);
            grid.add(createButton, 0, 4);
            grid.add(cancelButton, 1, 4);

            VBox wrapper = new VBox(grid);
            wrapper.setPadding(new Insets(10));

            Scene scene = new Scene(wrapper, 520, 280);
            popUp.setScene(scene);
            popUp.show();
        }

        private void createStudent() {
            String name = nameField.getText().trim();
            String studentId = studentIdField.getText().trim();
            String email = emailField.getText().trim();
            String facultyId = facultyIdField.getText().trim();

            if (!name.matches("[a-zA-Z ]+")) {
                showError("Please enter a valid name (letters and spaces only).");
                return;
            }

            if (!studentId.matches("[0-9]{8}")) {
                showError("Student ID must be exactly 8 digits.");
                return;
            }

            if (!email.endsWith("@ashesi.edu.gh")) {
                showError("Use your Ashesi email address (e.g. name@ashesi.edu.gh).");
                return;
            }

            if (!facultyId.matches("[0-9]{6}")) {
                showError("Faculty ID must be exactly 6 digits.");
                return;
            }

            if (studentExists(studentId, email)) {
                showError("Student already exists with that ID or email.");
                return;
            }

            Student student = new Student(name, studentId, email, facultyId);
            Student.saveStudentToCSV(student);
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Student account created successfully!");
            alert.showAndWait();
            popUp.close();
        }

        private boolean studentExists(String studentId, String email) {
            File file = new File("students.csv");
            if (!file.exists()) {
                return false;
            }
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line = reader.readLine();
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 3) {
                        if (parts[1].equals(studentId) || parts[2].equalsIgnoreCase(email)) {
                            return true;
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Error reading students file: " + e.getMessage());
            }
            return false;
        }

        private void showError(String message) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Validation Error");
            alert.setHeaderText("Check your details");
            alert.setContentText(message);
            alert.showAndWait();
        }
    }
}
