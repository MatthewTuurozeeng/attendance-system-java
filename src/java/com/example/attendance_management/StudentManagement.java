package com.example.attendance_management;

import javax.xml.stream.events.StartDocument;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


public class StudentManagement {
    private ArrayList<Student> students;
    private ArrayList<Faculty> faculty;
    private ArrayList<Course> courses;
    private Map<String, List<Student>> courseEnrollments;

    public StudentManagement() {
        this.students = new ArrayList<>();
        this.faculty = new ArrayList<>();
        this.courses = new ArrayList<>();
        this.courseEnrollments = new HashMap<>();
    }
    // Add a student to the system
    public void addStudent(Student student) {
        students.add(student);
    }
    // Add a faculty member
    public void addFaculty(Faculty faculty) {
        faculty.add(faculty);
    }
    // Add a course
    public void addCourse(Course course) {
        courses.add(course);
        courseEnrollments.put(course.getCourseID(), new ArrayList<>());
    }

    public ArrayList<Faculty> getFaculty() {
        return faculty;
    }

    public ArrayList<Course> getCourses() {
        return courses;
    }

    // Assign a course to a faculty member
    public void assignCourseToFaculty(Course courseID, Faculty faculty) {
        Optional<Course> course = courses.stream()
                .filter(c -> c.getCourseID().equals(courseID))
                .findFirst();
        if (course.isPresent()) {
            faculty.assignCourse(courseID);
            System.out.println("Course " + courseID + " assigned to faculty " + faculty.getName());
        } else {
            System.out.println("Error: Course not found.");
        }
    }

    // Enroll a student in a course (faculty controlling it )
    public String enrollStudentInCourse(String courseID, String studentID) {
        Optional<Student> student = students.stream()
                .filter(s -> s.getStudentId().equals(studentID) )
                .findFirst();
        if (student.isEmpty()) return "Error: Student not found.";

        Optional<Course> course = courses.stream()
                .filter(c -> c.getCourseID().equals(courseID))
                .findFirst();
        if (course.isEmpty()) return "Error: Course not found.";

        List<Student> enrolledStudents = courseEnrollments.get(courseID);
        if (enrolledStudents.contains(student.get())) {
            return "Error: Student is already enrolled in this course.";
        }
        enrolledStudents.add(student.get());
        return "Student " + student.get().getName() + " successfully enrolled in " + courseID + ".";
    }

    // View students in a course
    public List<Student> viewEnrolledStudents(String courseID) {
        return courseEnrollments.getOrDefault(courseID, new ArrayList<>());
    }

    // Remove a student from a course
    public String removeStudentFromCourse(String courseID, int studentID) {
        List<Student> enrolledStudents = courseEnrollments.get(courseID);
        if (enrolledStudents == null) return "Error: Course not found.";

        Optional<Student> student = enrolledStudents.stream()
                .filter(s -> s.getStudentId().equals(studentID))
                .findFirst();
        if (student.isEmpty()) return "Error: Student not enrolled in this course.";

        enrolledStudents.remove(student.get());
        return "Student " + student.get().getName() + " removed from course " + courseID + ".";
    }


}
