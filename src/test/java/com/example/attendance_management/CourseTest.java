package com.example.attendance_management;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CourseTest {
    @Test
    void acceptsValidCredits() {
        assertDoesNotThrow(() -> new Course("CS101", "Intro to CS", 1.0));
        assertDoesNotThrow(() -> new Course("CS102", "Programming", 0.5));
        assertDoesNotThrow(() -> new Course("CS103", "Lab", 0));
    }

    @Test
    void rejectsInvalidCredits() {
        assertThrows(IllegalArgumentException.class,
                () -> new Course("CS200", "Invalid Credit", 0.75));
    }
}
