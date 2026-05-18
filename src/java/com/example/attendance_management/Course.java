package com.example.attendance_management;

import java.util.ArrayList;
import java.util.List;

public class Course {
    public List<Student> studentEnrolled=new ArrayList<>();
    private String courseID;
    private  String courseName;
    private double courseCredit;

    public Course(String courseID,  String courseName, double courseCredit) {

        // Validate courseCredit
        if (courseCredit == 0 || courseCredit == 1.0 || courseCredit == 0.5) {
            // Initialize fields
            this.courseID = courseID;
            this.courseName = courseName;
            this.courseCredit = courseCredit;
        }else{
            throw new IllegalArgumentException("Course credit is invalid. Valid values are:  0, 0.5, or 1.0.");
        }



    }
    public String getCourseID() {
        return courseID;
    }

    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }
    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
    public double getCourseCredit() {
        return courseCredit;
    }

    public void setCourseCredit(int courseCredit) {
        this.courseCredit = courseCredit;
    }



    public String tostring(){
        return "Course ID: " + courseID +
                " Course Name: " + courseName + " Course Credit: " + courseCredit;
    }

    //add student method
    public void addStudent(Student st){
        studentEnrolled.add(st);
    }


    public List<Student> getEnrolledStudents() {
        return studentEnrolled;
    }

    public String getName() {
        return courseName;
    }
}
