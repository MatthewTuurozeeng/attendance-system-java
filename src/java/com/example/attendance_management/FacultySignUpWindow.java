package com.example.attendance_management;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;


// Nested PopUp class
public class FacultySignUpWindow {
    static Scene sn;
    private TextField course, courseID,name,em,password;
    Stage popUp;

    public void storeSignUpDetail() {
        popUp = new Stage();
        popUp.setTitle("Faculty Sign Up");

        // User areas
        GridPane gp = new GridPane();
        gp.setPadding(new Insets(20, 20, 20, 20));
        gp.setVgap(10);
        gp.setHgap(10);

        // Email
        em = new TextField();
        em.setPrefColumnCount(30);
        em.setPromptText("Enter your Ashesi  email: ");
        Label emailLabel = new Label("Email:");
        emailLabel.getStyleClass().addAll("label-text", "bold-text");

        // Password
        password = new TextField();
        password.setPrefColumnCount(10);
        password.setPromptText("Enter password: 6-8 characters(upper/lower) ");
        Label passwordLabel = new Label("Password:");
        passwordLabel.getStyleClass().addAll("label-text", "bold-text");

        // Name
        name = new TextField();
        name.setPromptText("Your full name");
        Label nameLabel = new Label("Name:");
        nameLabel.getStyleClass().addAll("label-text", "bold-text");

        // ID
        courseID = new TextField();
        courseID.setPromptText("Enter the course ID: ");
        Label idLabel = new Label("Course ID:");
        idLabel.getStyleClass().addAll("label-text", "bold-text");

        // Course
        course = new TextField();
        course.setPromptText("Enter the full name of your course: ");
        Label courseLabel = new Label("Course Name:");
        courseLabel.getStyleClass().addAll("label-text", "bold-text");

        // Buttons
        Button cancelButton = new Button("Cancel");
        Button create = new Button("Create Faculty");
        create.getStyleClass().add("button");

        // Capturing faculty details into a CSV file
        create.setOnAction(e ->{
            storeOnSignUp();
            Alert message=new Alert(Alert.AlertType.INFORMATION,"You have successfully created a faculty!" );
            message.showAndWait();
        });


        //When user clicks on the cancel button;
        cancelButton.getStyleClass().add("button");
        cancelButton.setOnAction(e -> {
            // Create a confirmation pop-up
            Alert confirmationStage = new Alert(Alert.AlertType.CONFIRMATION,
                    " Canceling may result in lose of your information! Are you sure? ");
            confirmationStage.setTitle("Confirm dialog");
            confirmationStage.setHeaderText(" Urgent!! ");
            // Buttons for "Okay" and "Cancel"
            Optional<ButtonType> result=confirmationStage.showAndWait();
            if(result.isPresent()&&result.get()==ButtonType.OK){
                popUp.close();
            }

            });



        // Arranging labels and text fields in the grid
        gp.add(emailLabel, 0, 0);
        gp.add(em, 1, 0);

        gp.add(passwordLabel, 0, 1);
        gp.add(password, 1, 1);

        gp.add(nameLabel, 0, 2);
        gp.add(name, 1, 2);

        gp.add(idLabel, 0, 3);
        gp.add(courseID, 1, 3);

        gp.add(courseLabel, 0, 4);
        gp.add(course, 1, 4);

        // Adding buttons
        gp.add(create, 0, 5);
        gp.add(cancelButton, 1, 5);

        // Adding the GridPane to a VBox and displaying it
        VBox vp = new VBox();
        vp.getChildren().add(gp);
        vp.setStyle("-fx-background-image: url('images/img.jpg'); " +
                "-fx-background-size: cover; " +  // Ensures the image covers the entire VBox
                "-fx-background-position: center;");
        sn = new Scene(vp, 612, 337);
        popUp.setScene(sn);
        popUp.show();
    }

// a method to validate faculty Sign up details;
    private void storeOnSignUp() {
        // Retrieve input from text fields
        String courseName = this.course.getText().trim();
        String courseId = this.courseID.getText().trim();
        String lecturerName = this.name.getText().trim();
        String email = this.em.getText().trim();
        String passwd = this.password.getText().trim();

        // Validate inputs
        if (lecturerName.isEmpty()||!nameIsValid(lecturerName)) {
            showError("Lecturer name cannot be empty.");
            return;
        }

        if (email.isEmpty() || !isValidEmail(email)) {
            showError("Please provide a valid email address.");
            return;
        }

        if(valInCSV(email)){
            showError("Email already exist, login instead if you're the authentic owner");
            return;
        }

        if (courseId.isEmpty()|!validateCourseID(courseId)) {
            showError("Invalid course ID.");
            return;
        }
        if (courseName.isEmpty()) {
            showError("Course name cannot be empty.");
            return;
        }
        if (passwd.isEmpty() ||!validPassword(passwd)) {
            showError("Password must be at least 6 characters long.Upper case,Special characters allowed");
            return;
        }

        // Store valid details
        HashMap<String, String> detail = new HashMap<>();
        detail.put(email, passwd);

        LecturerDetail faculty = new LecturerDetail(lecturerName, email, passwd,courseId, courseName);
        faculty.writeCSV();

        // Close the popup window
        popUp.close();
    }

    //validate name of faculty:
    public boolean nameIsValid(String name) {
        String regex = "^[A-Z][a-z]+( [A-Z][a-z]+)? [A-Z][a-z]+$";
        return name.matches(regex);
    }

    //Validating the password
    public boolean validPassword(String pass){
        String regex ="^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[+_@&#])[a-zA-Z0-9+_@&#]{6,12}$";
        return pass.matches(regex);
    }
    // A method to validate the course id;
    public boolean validateCourseID(String courseID){
        String pattern="^[A-Z]{2,6}[0-9]{3}";
        return courseID.matches(pattern);
    }
    // Alert to be displayed when something goes wrong;
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validation Error");
        alert.setHeaderText(" Very important ! ");
        alert.setContentText(message);
        alert.showAndWait();
    }
    //Validating special cases in email verification
    private boolean isValidEmail(String email) {
        String emailPattern = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailPattern);
    }


    public static boolean valInCSV(String value) {
        String path = "facultyDetails.csv";
        try (BufferedReader rdr = new BufferedReader(new FileReader(path))) {
            String line;

            // Skip the header
            if ((line = rdr.readLine()) == null) {
                System.out.println("CSV file is empty or only contains a header.");
                return false;
            }

            // Process each line
            while ((line = rdr.readLine()) != null) {
                String[] parts = line.split(","); // Split the line into columns
                if (Arrays.asList(parts).contains(value)) { // Check if the value exists in the row
                    return true;
                }
            }

        } catch (IOException e) {
            System.out.println("Error reading the file: " + e.getMessage());
        }

        return false; // Return false if no match is found
    }


}
