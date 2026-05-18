package com.example.attendance_management;

import java.io.*;
import java.util.*;

public class ManagementDriver {
    static Map<String, List<String>> courses;
    static List<Faculty> facultyList = new ArrayList<>();
    static List<Student> studentList = new ArrayList<>();

    public static void main(String[] args) {
        studentList=loadFromCSV();
        facultyList=Faculty.loadFacultyFromCSV();
        Scanner scanner = new Scanner(System.in);
        String message = "WELCOME TO EDU-TRACK'S STUDENT MANAGEMENT SYSTEM WHERE YOU CAN TRACK  YOUR STUDENT ATTENDANCE PROGRESS ";
        System.out.println(message);
        System.out.println("Please select an option to continue");
        boolean running = true;

        while (running) {
            System.out.println("\n1. Add faculty");
            System.out.println("2. Mark Attendance");
            System.out.println("3. View Progress");
            System.out.println("4. Add a New Student to your course");
            System.out.println("5. Exit");

            System.out.print("Enter your choice: ");

            int choice1 = scanner.nextInt();
            scanner.nextLine(); // Clear buffer
            switch (choice1) {
                    // Add Faculty
                case 1:
                    System.out.println("Please enter your faculty details: ");

                    String name;
                    do {
                        System.out.print("Enter the name: ");
                        name = scanner.nextLine();
                        // Only letters and spaces
                        if (!name.matches("[a-zA-Z ]+")) {
                            System.out.println("Invalid name. Please use letters and spaces only.");
                        }
                    } while (!name.matches("[a-zA-Z ]+"));

                    String id;
                    do {
                        System.out.print("Enter the ID: ");
                        id = scanner.nextLine();
                        if (id.trim().isEmpty()||!id.matches("[0-9]{6}")) {
                            System.out.println("Invalid ID. Please try again.");
                        }
                    } while (id.trim().isEmpty()||!id.matches("[0-9]{6}"));

                    String email;
                    do {
                        System.out.print("Enter the email: ");
                        email = scanner.nextLine();
                        if (!email.matches("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2}$")) {
                            System.out.println("Invalid email format. Please try again.");
                        }
                    } while (!email.matches("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2}$"));

                    System.out.println("Enter the department: ");
                    displayDepartments();
                    int departmentChoice;
                    do {
                        System.out.print("Choose a department (1-" + courses.size() + "): ");
                        while (!scanner.hasNextInt()) {
                            System.out.println("Invalid input. Enter a number.");
                            scanner.next(); // Clear invalid input
                        }
                        departmentChoice = scanner.nextInt();
                    } while (departmentChoice < 1 || departmentChoice > courses.size());

                    scanner.nextLine(); // Clear buffer
                    displayCourses(departmentChoice - 1);
                    int courseChoice;
                    do {
                        System.out.print("Choose a course: ");
                        while (!scanner.hasNextInt()) {
                            System.out.println("Invalid input. Enter a number.");
                            scanner.next();
                        }
                        courseChoice = scanner.nextInt();
                    } while (courseChoice < 1 || courseChoice > courses.get(new ArrayList<>(courses.keySet()).get(departmentChoice - 1)).size());

                    scanner.nextLine();
                    String departmentName = new ArrayList<>(courses.keySet()).get(departmentChoice - 1);
                    String courseName = courses.get(departmentName).get(courseChoice - 1);

                    double courseCredit;
                    do {
                        System.out.print("Enter course credit (0, 0.5, or 1.0): ");
                        while (!scanner.hasNextDouble()) {
                            System.out.println("Invalid input. Enter a number (0, 0.5, or 1.0).");
                            scanner.next();
                        }
                        courseCredit = scanner.nextDouble();
                        scanner.nextLine();
                    } while (courseCredit != 0 && courseCredit != 0.5 && courseCredit != 1.0);

                    try {
                        Course course = new Course(id, courseName, courseCredit);
                        Faculty newFaculty = new Faculty(id, name, email, course.getName());
                        Faculty.saveFacultyDetailsToCSV(newFaculty);
                        facultyList.add(newFaculty);
                        System.out.println("Faculty member added successfully!");
                    } catch (IllegalArgumentException e) {
                        System.out.println("Error creating course: " + e.getMessage());
                    }
                    break;


                case 2:
                    // Mark Attendance
                    System.out.print("Enter the Faculty ID : ");
                    String fID= scanner.nextLine();
                    for(Student st: studentList) {
                        if(fID.equals(st.getUniqueCode())){
                            System.out.print("Is the student: " +st.getName()+" present? (true/false): ");
                            boolean present = scanner.nextBoolean();
                            scanner.nextLine(); // Clear buffer
                            st.attendanceProperty().set(present);
                            Student.saveStudentToCSV(st); // Save updated record to CSV
                        }
                    }
                    System.out.println("Attendance marked successfully!");

                    break;

                    // Viewing student Progress;
                case 3:
                    System.out.print("Enter your faculty ID: ");
                    String  facultyId = scanner.nextLine();
                    System.out.println("\n Students in your class are: ");
                    for(Student st: studentList)
                    {
                        if(facultyId.equals(st.getUniqueCode())){
                            System.out.println("Student Name: " + st.getName());
                            System.out.println("Student ID: " + st.getStudentId());
                            System.out.println("Email: " + st.getEmail());
                            System.out.println("Attendance: " + st.attendanceProperty().get());
                            System.out.println("Attendance Percentage: " + st.attendancePercentageProperty().get() + "%");
                            System.out.println(" ");
                        }

                    }
                    break;



                case 4:
                    System.out.print("Enter your faculty ID to add a student to your course: ");
                    Faculty faculty;
                    do {
                        facultyId = scanner.nextLine();
                        faculty = findFacultyById(facultyId);
                        if (faculty == null) {
                            System.out.println("Invalid faculty ID. Please try again.");
                        }
                    } while (faculty == null);

                    String studentName;
                    do {
                        System.out.print("Enter the student name: ");
                        studentName = scanner.nextLine();
                        if (!studentName.matches("[a-zA-Z ]+")) {
                            System.out.println("Invalid name. Please use letters and spaces only.");
                        }
                    } while (!studentName.matches("[a-zA-Z ]+"));

                    String newStudentId;
                    do {
                        System.out.print("Enter the student ID: ");
                        newStudentId = scanner.nextLine();
                        if (newStudentId.trim().isEmpty()||!newStudentId.matches("[0-9]{8}")) {
                            System.out.println("Student ID cannot be empty.");
                        }
                    } while (newStudentId.trim().isEmpty()||!newStudentId.matches("[0-9]{8}"));

                    String studentEmail;
                    do {
                        System.out.print("Enter the email: ");
                        studentEmail = scanner.nextLine();
                        if (!studentEmail.matches("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2}$")) {
                            System.out.println("Invalid email format. Please try again.");
                        }
                    } while (!studentEmail.matches("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2}$"));

                    Student newStudent = new Student(studentName, newStudentId, studentEmail, facultyId);
                    studentList.add(newStudent);
                    Student.saveStudentToCSV(newStudent);
                    System.out.println("Student added successfully under your course!");
                    break;


                case 5:
                    System.out.println("It's nice having you !. See you next time !");
                    running =false;
                    break;


                default:
                    System.out.println("Invalid choice. Please choose from one of the options above");
            }
        }

        scanner.close(); // Close scanner
    }


    public static void displayDepartments() {
        courses = new HashMap<>();

        courses.put("Engineering", Arrays.asList(
                "Computer Programming",
                "Circuits and Electronics",
                "Digital Systems Design",
                "Engineering Mathematics",
                "Signals and Systems",
                "Control Systems",
                "Thermodynamics",
                "Mechanics of Materials",
                "Robotics and Embedded Systems"
        ));

        courses.put("Computer Science", Arrays.asList(
                "Data Structures and Algorithms",
                "Database Systems",
                "Artificial Intelligence",
                "Machine Learning",
                "Operating Systems",
                "Computer Networks",
                "Software Engineering",
                "Cybersecurity",
                "Mobile Application Development",
                "Web Technologies"
        ));

        courses.put("Business Administration", Arrays.asList(
                "Financial Accounting",
                "Principles of Management",
                "Marketing Management",
                "Business Law",
                "Corporate Finance",
                "Entrepreneurship",
                "Operations Management",
                "Strategic Management",
                "Human Resource Management"
        ));

        courses.put("Management Information Systems (MIS)", Arrays.asList(
                "Information Systems Analysis and Design",
                "Enterprise Resource Planning",
                "Data Visualization",
                "IT Project Management",
                "E-commerce Systems",
                "Business Intelligence",
                "Data Analytics"
        ));

        courses.put("Humanities and Social Sciences", Arrays.asList(
                "African Philosophical Thought",
                "Development Studies",
                "Critical Thinking and Reasoning",
                "Global Politics and Governance",
                "Ethics in Leadership",
                "History of Africa"
        ));

        courses.put("Liberal Arts (Core Courses)", Arrays.asList(
                "Leadership Seminar",
                "Foundations of Design and Entrepreneurship (FDE)",
                "Research Methods",
                "Writing and Communication Skills",
                "Statistics for Decision Making"
        ));

        // Display departments
        Set<String> departments = courses.keySet();
        int i = 1;
        for (String department : departments) {
            System.out.println(i + ". " + department);
            i++;
        }
    }

    public static void displayCourses(int index) {
        // Get the department name based on the index
        String departmentName = new ArrayList<>(courses.keySet()).get(index);
        List<String> departmentCourses = courses.get(departmentName);

        for (int i = 0; i < departmentCourses.size(); i++) {
            System.out.println((i + 1) + ". " + departmentCourses.get(i));
        }
    }

    public static Faculty findFacultyById(String id) {
        for (Faculty faculty : facultyList) {
            if (faculty.getId().equals(id)) {
                return faculty;
            }
        }
        return null; // Not found
    }

    public static Student findStudentById(String id) {
        for (Student student : studentList) {
            if (student.getStudentId().equals(id)) {
                return student;
            }
        }
        System.out.println("No student with Id:"+id+"found!");
        return null; // Not found
    }

   // method to load data from csv into our studentList;
    public static List<Student> loadFromCSV() {
        String path = "students.csv";
        File file = new File(path);
        List <Student> studentList = new ArrayList<>();

        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String header = br.readLine(); // Skip the header line
                String line;

                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 6) { // Ensure all fields are present
                        String name = parts[0];
                        String studentId = parts[1];
                        String email = parts[2];
                        String facultyId = parts[3];
                        boolean attendance = Boolean.parseBoolean(parts[4]);
                        int attendancePercentage = Integer.parseInt(parts[5]);

                        // Create Student object
                        Student student = new Student(name, studentId, email, facultyId);
                        student.attendanceProperty().set(attendance);
                        student.attendancePercentageProperty().set(attendancePercentage);
                        studentList.add(student);
                    }
                }
            } catch (IOException | NumberFormatException e) {
                System.err.println("Error loading students from file: " + e.getMessage());
            }
        } else {
            System.out.println("File not found: " + path);
        }

        return studentList;
    }

}
