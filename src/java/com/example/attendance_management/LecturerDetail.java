package com.example.attendance_management;

import java.io.*;
import java.util.ArrayList;

public class LecturerDetail {
    // Shared list of all lecturer details
    public  ArrayList<LecturerDetail> details = new ArrayList<>();

    // Final static header for CSV
    private static final String[] HEADER = {"Name", "Email", "Password", "Course ID", "Course Name"};

    private String name;
    private String email;
    private String password;
    private String courseCode;
    private String course;

    // Constructor
    public LecturerDetail(String name, String email, String password, String courseCode, String course) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.courseCode = courseCode;
        this.course = course;
        details.add(this);
    }

    // Method to write data to CSV
    public  void writeCSV() {
        File facultyCSV = new File("facultyDetail.csv");
        boolean fileExists = facultyCSV.exists();

        try (FileWriter fw = new FileWriter(facultyCSV, true);
             PrintWriter pw = new PrintWriter(fw)) {

            // Write header if the file is new
            if (!fileExists) {
                pw.println(String.join(",", HEADER));
            }

            // Write new details
            for (LecturerDetail d : details) {
                String[] data = {d.name, d.email, d.password, d.courseCode, d.course};
                pw.println(String.join(",", data));
            }

            // Clear details after writing to avoid duplicates
            details.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
