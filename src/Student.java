import java.security.KeyPair;
import java.util.*;

public class Student {
    String full_name;
    String id;
    String educational_institution;
    List<String> subject;
    List<String> grade;

    public Student(){
        String full_name = "";
        String id = "";
        String educational_institution = "";
        subject= new ArrayList<>();
        grade= new ArrayList<>();
    }

    public Student(String full_name, String id, String educational_institution){
        this.full_name = full_name;
        this.id = id;
        this.educational_institution = educational_institution;
        subject= new ArrayList<>();
        grade= new ArrayList<>();
    }

    public void addGrade(String subject, String grade ){
        this.subject.add(subject);
        this.grade.add(grade);
    }

}
