import java.util.*;
import java.io.*;
import java.text.SimpleDateFormat;

public class AttendanceTracker {
    private static final String STUDENT_FILE = "students.txt";
    private static final String ATTENDANCE_FILE = "attendance.csv";

    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n1) Add student\n2) List students\n3) Mark attendance (today)\n4) Show attendance % for a student\n5) Show all percentages\n6) Exit");
            System.out.print("Choose: ");
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1": addStudent(sc); break;
                case "2": listStudents(); break;
                case "3": markAttendance(sc); break;
                case "4": showPercentageForStudent(sc); break;
                case "5": showAllPercentages(); break;
                case "6": System.out.println("Bye"); sc.close(); return;
                default: System.out.println("Invalid choice");
            }
        }
    }

    private static void addStudent(Scanner sc) throws IOException {
        System.out.print("Enter Student ID: ");
        String id = sc.nextLine().trim();
        if (id.isEmpty()) { System.out.println("ID required"); return; }
        System.out.print("Enter Student Name: ");
        String name = sc.nextLine().trim();
        if (name.isEmpty()) { System.out.println("Name required"); return; }
        if (existsStudent(id)) { System.out.println("Student ID already exists"); return; }
        try (FileWriter fw = new FileWriter(STUDENT_FILE, true)) {
            fw.write(id + "," + name + System.lineSeparator());
        }
        System.out.println("Student added");
    }

    private static boolean existsStudent(String id) throws IOException {
        List<Student> list = readAllStudents();
        for (Student s : list) if (s.getId().equals(id)) return true;
        return false;
    }

    private static List<Student> readAllStudents() throws IOException {
        List<Student> list = new ArrayList<>();
        File f = new File(STUDENT_FILE);
        if (!f.exists()) return list;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                Student s = Student.fromCSV(line);
                if (s != null) list.add(s);
            }
        }
        return list;
    }

    private static void listStudents() throws IOException {
        List<Student> list = readAllStudents();
        if (list.isEmpty()) { System.out.println("No students found"); return; }
        System.out.println("\nID\tName");
        for (Student s : list) System.out.println(s.getId() + "\t" + s.getName());
    }

    private static void markAttendance(Scanner sc) throws IOException {
        List<Student> students = readAllStudents();
        if (students.isEmpty()) { System.out.println("No students to mark"); return; }
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        Map<String, String> today = new HashMap<>();
        System.out.println("Date: " + date + " (P = present, A = absent)");
        for (Student s : students) {
            System.out.print(s.getId() + " - " + s.getName() + " : ");
            String ans = sc.nextLine().trim().toUpperCase();
            if (!(ans.equals("P") || ans.equals("A"))) ans = "A";
            today.put(s.getId(), ans);
        }
        boolean fileExists = new File(ATTENDANCE_FILE).exists();
        try (FileWriter fw = new FileWriter(ATTENDANCE_FILE, true)) {
            if (!fileExists) fw.write("date,studentId,status\n");
            for (Map.Entry<String, String> e : today.entrySet()) {
                fw.write(date + "," + e.getKey() + "," + e.getValue() + System.lineSeparator());
            }
        }
        System.out.println("Attendance saved for " + date);
    }

    private static void showPercentageForStudent(Scanner sc) throws IOException {
        System.out.print("Enter Student ID: ");
        String id = sc.nextLine().trim();
        if (!existsStudent(id)) { System.out.println("Student not found"); return; }
        Map<String, Integer> counts = attendanceCounts();
        int present = counts.getOrDefault(id + "_P", 0);
        int total = counts.getOrDefault(id + "_T", 0);
        if (total == 0) { System.out.println("No attendance records for this student"); return; }
        double percent = (present * 100.0) / total;
        System.out.printf("Attendance for %s: %d/%d (%.2f%%)%n", id, present, total, percent);
    }

    private static Map<String, Integer> attendanceCounts() throws IOException {
        Map<String, Integer> counts = new HashMap<>();
        File f = new File(ATTENDANCE_FILE);
        if (!f.exists()) return counts;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",", 3);
                if (p.length < 3) continue;
                String studentId = p[1];
                String status = p[2];
                counts.put(studentId + "_T", counts.getOrDefault(studentId + "_T", 0) + 1);
                if (status.equals("P")) counts.put(studentId + "_P", counts.getOrDefault(studentId + "_P", 0) + 1);
            }
        }
        return counts;
    }

    private static void showAllPercentages() throws IOException {
        List<Student> students = readAllStudents();
        if (students.isEmpty()) { System.out.println("No students"); return; }
        Map<String, Integer> counts = attendanceCounts();
        System.out.println("\nID\tName\tPresent/Total\t%"); 
        for (Student s : students) {
            String id = s.getId();
            int present = counts.getOrDefault(id + "_P", 0);
            int total = counts.getOrDefault(id + "_T", 0);
            double percent = total == 0 ? 0.0 : (present * 100.0) / total;
            System.out.printf("%s\t%s\t%d/%d\t%.2f%%%n", id, s.getName(), present, total, percent);
        }
    }
}
