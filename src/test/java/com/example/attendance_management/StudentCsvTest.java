package com.example.attendance_management;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class StudentCsvTest {
    @TempDir
    Path tempDir;

    private String originalUserDir;
    private Path studentsFile;
    private byte[] backup;
    private boolean hadFile;

    @BeforeEach
    void setUp() {
        originalUserDir = System.getProperty("user.dir");
        System.setProperty("user.dir", tempDir.toString());
        studentsFile = Path.of("").toAbsolutePath().resolve("students.csv");
        try {
            hadFile = java.nio.file.Files.exists(studentsFile);
            if (hadFile) {
                backup = java.nio.file.Files.readAllBytes(studentsFile);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    void tearDown() {
        if (originalUserDir != null) {
            System.setProperty("user.dir", originalUserDir);
        }
        restoreFile();
    }

    @Test
    void savesAndLoadsStudentRecord() {
        Student student = new Student("Ada Lovelace", "12345678", "ada@ashesi.edu.gh", "FAC001");
        student.attendanceProperty().set(true);
        student.attendancePercentageProperty().set(98);

        Student.saveStudentToCSV(student);

    var loaded = Student.loadFromCSV();
    assertNotNull(loaded);
    boolean found = loaded.stream().anyMatch(entry ->
        "Ada Lovelace".equals(entry.getName())
            && "12345678".equals(entry.getStudentId())
            && "ada@ashesi.edu.gh".equals(entry.getEmail())
            && "FAC001".equals(entry.getUniqueCode())
            && entry.attendancePercentageProperty().get() == 98);
    assertEquals(true, found);
    }

    private void restoreFile() {
        try {
            if (hadFile) {
                java.nio.file.Files.write(studentsFile, backup);
            } else if (java.nio.file.Files.exists(studentsFile)) {
                java.nio.file.Files.delete(studentsFile);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
