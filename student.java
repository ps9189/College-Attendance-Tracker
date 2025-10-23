import java.io.Serializable;

public class Student implements Serializable {
    private String id;
    private String name;
    public Student(String id, String name) {
        this.id = id;
        this.name = name;
    }
    public String getId() { return id; }
    public String getName() { return name; }
    public String toCSV() { return id + "," + name; }
    public static Student fromCSV(String line) {
        String[] p = line.split(",", 2);
        if (p.length < 2) return null;
        return new Student(p[0], p[1]);
    }
}
