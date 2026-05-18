package com.example.attendance_management;

import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(ApplicationExtension.class)
class SceneOneUiTest {
    private static final String FACULTY_ID = "123456";
    private static final String FACULTY_EMAIL = "test@ashesi.edu.gh";

    @TempDir
    Path tempDir;

    private String originalUserDir;
    private Path facultyFile;
    private Path studentsFile;
    private byte[] facultyBackup;
    private byte[] studentsBackup;
    private boolean hadFacultyFile;
    private boolean hadStudentsFile;

    @AfterEach
    void tearDown() {
        if (originalUserDir != null) {
            System.setProperty("user.dir", originalUserDir);
        }
        if (facultyFile != null) {
            restoreFile(facultyFile, facultyBackup, hadFacultyFile);
        }
        if (studentsFile != null) {
            restoreFile(studentsFile, studentsBackup, hadStudentsFile);
        }
        try {
            FxToolkit.cleanupStages();
        } catch (Exception ignored) {
            // Ignore cleanup timeouts in headful UI tests.
        }
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Start
    private void start(Stage stage) throws Exception {
        if (originalUserDir == null) {
            originalUserDir = System.getProperty("user.dir");
        }
        Path workingDir = Path.of("").toAbsolutePath();
        facultyFile = workingDir.resolve("facultyDetails.csv");
        studentsFile = workingDir.resolve("students.csv");
        try {
            hadFacultyFile = Files.exists(facultyFile);
            hadStudentsFile = Files.exists(studentsFile);
            if (hadFacultyFile) {
                facultyBackup = Files.readAllBytes(facultyFile);
            }
            if (hadStudentsFile) {
                studentsBackup = Files.readAllBytes(studentsFile);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        writeFacultyCsv(facultyFile.getParent());
        SceneOne sceneOne = new SceneOne();
        sceneOne.launchPage(stage);
    }

    @Test
    void loginShowsFacultyArea(FxRobot robot) {
        robot.clickOn("#emailField").write(FACULTY_EMAIL);
        robot.clickOn("#idField").write(FACULTY_ID);
        robot.clickOn("#loginButton");

        WaitForAsyncUtils.waitForFxEvents();
        TableView<?> table = robot.lookup("#studentTable").queryAs(TableView.class);
        assertNotNull(table);
    }

    @Test
    void addStudentCreatesRow(FxRobot robot) {
        robot.clickOn("#emailField").write(FACULTY_EMAIL);
        robot.clickOn("#idField").write(FACULTY_ID);
        robot.clickOn("#loginButton");

        WaitForAsyncUtils.waitForFxEvents();
        robot.clickOn("#addStudentButton");

        robot.clickOn("#studentNameField").write("Ada Lovelace");
        robot.clickOn("#studentIdField").write("20240001");
        robot.clickOn("#studentEmailField").write("ada@ashesi.edu.gh");
        robot.clickOn("#facultyIdField").write(FACULTY_ID);
        robot.clickOn("#createStudentButton");

        WaitForAsyncUtils.waitForFxEvents();
        TableView<?> table = robot.lookup("#studentTable").queryAs(TableView.class);
        assertNotNull(table);
        assertEquals(1, table.getItems().size());
    }

    private void writeFacultyCsv(Path baseDir) throws IOException {
        Path file = baseDir.resolve("facultyDetails.csv");
        String header = "ID,Name,Email,Course Teaching";
        String row = String.join(",", FACULTY_ID, "Test Faculty", FACULTY_EMAIL, "Data Structures");
        Files.writeString(file, header + System.lineSeparator() + row + System.lineSeparator());
    }

    private void restoreFile(Path file, byte[] backup, boolean hadFile) {
        try {
            if (hadFile) {
                Files.write(file, backup);
            } else if (Files.exists(file)) {
                Files.delete(file);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
