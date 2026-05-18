package com.example.attendance_management;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FacultyCsvTest {
    @TempDir
    Path tempDir;

    private String originalUserDir;
    private Path facultyFile;
    private byte[] backup;
    private boolean hadFile;

    @BeforeEach
    void setUp() {
        originalUserDir = System.getProperty("user.dir");
        System.setProperty("user.dir", tempDir.toString());
        facultyFile = Path.of("").toAbsolutePath().resolve("facultyDetails.csv");
        try {
            hadFile = java.nio.file.Files.exists(facultyFile);
            if (hadFile) {
                backup = java.nio.file.Files.readAllBytes(facultyFile);
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
    void savesAndLoadsFacultyRecord() {
        Faculty faculty = new Faculty("FAC001", "Dr. Mensah", "mensah@ashesi.edu.gh", "Data Structures");
        Faculty.saveFacultyDetailsToCSV(faculty);

    List<Faculty> loaded = Faculty.loadFacultyFromCSV();
    assertNotNull(loaded);
    boolean found = loaded.stream().anyMatch(entry ->
        "FAC001".equals(entry.getId())
            && "Dr. Mensah".equals(entry.getName())
            && "mensah@ashesi.edu.gh".equals(entry.getEmail())
            && "Data Structures".equals(entry.getCourse()));
    assertEquals(true, found);
    }

    private void restoreFile() {
        try {
            if (hadFile) {
                java.nio.file.Files.write(facultyFile, backup);
            } else if (java.nio.file.Files.exists(facultyFile)) {
                java.nio.file.Files.delete(facultyFile);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
