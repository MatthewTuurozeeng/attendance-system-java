package com.example.attendance_management;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Faculty extends Person{
    private List<Student> studentEnrolled=new ArrayList<>();
    private final String id;
    private  String course;
    private final Map<Student, Map<String, List<Double>>> grades;

    public Faculty(String id, String name, String email, String course) {
        super(name,email);
        this.id = id;
        this.course = course;
        this.grades = new HashMap<>();
    }
    public String getId(){
        return id;}
    public String getCourse(){
        return course;
    }

    public void markAttendance(Student student, Course course, Attendance attendance, boolean present) {
        if (studentInThisCourse(student,this)) {
            attendance.markAttendance(present);
            System.out.println("Attendance marked for " + student.getName() + " in " + course.getCourseName());
        } else {
            System.out.println("Cannot mark attendance: Not your course.");
        }
    }

    public void assignGrades(Student student, String gradeType, double score) {
        grades.computeIfAbsent(student, k -> new HashMap<>())
                .computeIfAbsent(gradeType, k -> new ArrayList<>())
                .add(score);
        System.out.println("Assigned " + gradeType + " grade (" + score + ") to " + student.getName());
    }

    public void assignGradesInteractive(String gradeType, List<Student> students, Scanner scanner) {
        for (Student student : students) {
            System.out.print("Enter grade for " + student.getName() + " (" + gradeType + "): ");
            double score = scanner.nextDouble();
            assignGrades(student, gradeType, score);
        }
    }

    public void getStudentProgress(Student student, Attendance attendance) {
        System.out.println("\n--- Progress for " + student.getName() + " ---");
        System.out.println(attendance);
        if (grades.containsKey(student)) {
            System.out.println("Grades:");
            grades.get(student).forEach((type, scores) -> {
                System.out.println(type + ": " + scores);
            });
        } else {
            System.out.println("No grades recorded.");
        }
    }
    // a method to keep track of student under this faculty and save it to csv
//
//    public void saveThisStudentsToCSV() {
//        // Define the file path using the course name
//        String path = courseTeaching.getCourseName() + ".csv";
//        File file = new File(path);
//        // Try-with-resources for safe file handling
//        try (PrintWriter pw = new PrintWriter(file)) {
//            // Write CSV header
//            pw.println("Name,ID,Email,Status,Attendance(in %)");
//
//            // Write student data to the CSV file
//            for (Student student : studentEnrolled) {
//                pw.println(student.getName()+","+ student.getStudentId() + ","
//                        + student.getEmail()+","+student.attendanceProperty()+","+student.attendancePercentageProperty());
//            }
//
//            System.out.println("Students saved successfully to: " + path);
//        } catch (Exception e) {
//            System.err.println("Error saving students to CSV: " + e.getMessage());
//        }
//    }
    public void add(Faculty faculty) {
    }

    public void assignCourse(Course courseID) {

    }
    private boolean studentInThisCourse(Student st,Faculty f){
        return  (grades.containsKey(st)) ;
    }

    public List<Student> getStudentEnrolled() {
        return studentEnrolled;
    }


    public void setStudentEnrolled(List<Student> studentEnrolled) {
        this.studentEnrolled = studentEnrolled;
    }
    public static void saveFacultyDetailsToCSV(Faculty faculty) {
        String path = "facultyDetails.csv";
        File file = new File(path);
        List<String> listOfFacultyDetails = new ArrayList<>();
        String header = "ID,Name,Email,Course Teaching";

        // Read existing records or prepare for new file
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) { // Ignore empty lines
                    listOfFacultyDetails.add(line);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found. A new file will be created.");
            listOfFacultyDetails.add(header); // Add header for a new file
        } catch (IOException e) {
            System.out.println("Error reading the file: " + e.getMessage());
            return; // Exit on failure to read the file
        }

        boolean facultyExists = false;

        // Update existing record or add a new one
        for (int i = 1; i < listOfFacultyDetails.size(); i++) { // Skip header
            String[] parts = listOfFacultyDetails.get(i).split(",");
            if (parts.length >= 2 && parts[1].equals(faculty.getId())) { // Check index safety
                listOfFacultyDetails.set(i, formatFacultyRecord(faculty));
                facultyExists = true;
                break;
            }
        }

        if (!facultyExists) {
            listOfFacultyDetails.add(formatFacultyRecord(faculty));
        }

        // Write back the updated records
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            for (String line : listOfFacultyDetails) {
                writer.println(line);
            }
            //System.out.println("faculty record saved successfully!");
        } catch (IOException e) {
            System.out.println("Error saving faculty to CSV: " + e.getMessage());
        }
    }

    private static String formatFacultyRecord(Faculty faculty) {
        return String.join(",",
                faculty.getId(),
                faculty.getName(),
                faculty.getEmail(),
                faculty.getCourse().toString());
    }

    public static List<Faculty> loadFacultyFromCSV() {
        String path = "facultyDetails.csv";
        File file = new File(path);
        List <Faculty> facultyList = new ArrayList<>();

        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String header = br.readLine(); // Skip the header line
                String line;

                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 4) { // Ensure all fields are present
                        String facultyId = parts[0];
                        String name = parts[1];
                        String email = parts[2];
                        String course= parts[3];
                        // Create Student object
                        Faculty faculty = new Faculty(facultyId,name, email,course);
                        facultyList.add(faculty);
                    }
                }
            } catch (IOException | NumberFormatException e) {
                System.err.println("Error loading students from file: " + e.getMessage());
            }
        } else {
            System.out.println("File not found: " + path);
        }

        return facultyList;
    }


// convert course string back to its original form ;
        public static Course parse(String courseString) {
            String[] parts = courseString.split(":"); // Example: "Math101:Mathematics 101"
            if (parts.length == 2) {
                String courseCode = parts[0].trim();
                String courseName = parts[1].trim();
                int courseCredit=Integer.parseInt(parts[2]);
                return new Course(courseCode,courseName, courseCredit);
            } else {
                throw new IllegalArgumentException("Invalid course format: " + courseString);
            }
        }




}

