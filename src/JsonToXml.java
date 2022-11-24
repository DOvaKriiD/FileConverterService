import org.json.JSONObject;
import org.json.XML;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.json.JSONException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class JsonToXml {
    private static List<Student> readFromJson(String tempFilePath){
        List<Student> students = new ArrayList<>();

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {

            dbf.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new File(tempFilePath));
            doc.getDocumentElement().normalize();

            NodeList institutionList = doc.getElementsByTagName("student");
            for (int temp = 0; temp < institutionList.getLength(); temp++) {


                Node studentsList = institutionList.item(temp);
                if (studentsList.getNodeType() == Node.ELEMENT_NODE) {
                    Element studentElem = (Element) studentsList;
                    //Node studentElem = studentsElem.item(0);

                    String full_name = studentElem.getElementsByTagName("full_name").item(0).getTextContent();
                    String id = studentElem.getElementsByTagName("id").item(0).getTextContent();
                    String educational_institution = studentElem.getElementsByTagName("educational_institution").item(0).getTextContent();
                    Student tempStudent = new Student(full_name,id,educational_institution);

                    NodeList gradeList = studentElem.getElementsByTagName("grade");
                    for (int grd = 0; grd < gradeList.getLength(); grd++){
                        Node gradeNode = gradeList.item(grd);
                        if (gradeNode.getNodeType() == Node.ELEMENT_NODE){
                            Element gradeElem = (Element) gradeNode;
                            tempStudent.addGrade(gradeElem.getElementsByTagName("subject").item(0).getTextContent(),
                                    gradeElem.getTextContent().substring(gradeElem.getElementsByTagName("subject").item(0).getTextContent().length()));
                        }
                    }

                    students.add(tempStudent);
                }
            }

        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }


        return students;
    }

    private static void ListToXml(List<Student> students,String xmlPath)throws ParserConfigurationException, TransformerException, IOException{
        List<List<Integer>> nodes = new ArrayList<>();
        List<String> institutions = new ArrayList<>();


        boolean flag;
        for(int i = 0; i<students.size();i++){
            flag = false;
            for(int j=0;j<nodes.size();j++){
                if(students.get(i).educational_institution.equals(institutions.get((j)))){
                    nodes.get(j).add(i);
                    flag = true;
                }
            }
            if (!flag){
                nodes.add(new ArrayList<>());
                nodes.get(nodes.size()-1).add(i);
                institutions.add(students.get(i).educational_institution);

            }
        }

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("educational_establishments");
        doc.appendChild(rootElement);

        for (int i=0; i < institutions.size();i++){
            Element educational_institutionElem = doc.createElement("educational_institution");
            rootElement.appendChild(educational_institutionElem);
            educational_institutionElem.setAttribute("title", institutions.get(i));
            Element studentsElem = doc.createElement("students");
            educational_institutionElem.appendChild(studentsElem);

            for(int j=0;j<nodes.get(i).size();j++){
                Element  studentElem = doc.createElement("student");
                studentElem.setAttribute("id", Integer.toString(j+1));
                studentsElem.appendChild(studentElem);
                Element nameElem = doc.createElement("full_name");
                nameElem.setTextContent(students.get(j).full_name);
                studentElem.appendChild(nameElem);
                Element gradesElem = doc.createElement("grades");
                studentElem.appendChild(gradesElem);
                for(int l = 0;l<students.get(j).grade.size();l++){
                    Element grade = doc.createElement("grade");
                    grade.setAttribute("subject", students.get(j).subject.get(l));
                    grade.setTextContent(students.get(j).grade.get(l));
                    gradesElem.appendChild(grade);
                }
            }
        }


        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new FileOutputStream(xmlPath));

        transformer.transform(source, result);

    }



    private static String supportConvertToXML(String jsonString, String root) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonString);

        String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<"+root+">" + XML.toString(jsonObject) + "</"+root+">";

        return xml;
    }

    public static void jsonToXml(String jsonPath, String xmlPath) throws ParserConfigurationException, IOException, TransformerException {
        String TempPath = "D:\\temp.xml";

        String loc = jsonPath;

        String result;
        try {

            result = new String(Files.readAllBytes(Paths.get(loc)));
            String xml = supportConvertToXML(result, "educational_establishments"); // This method converts json object to xml string

            FileWriter file = new FileWriter(TempPath);

            file.write(xml);
            file.flush();

            file.close();

        } catch (IOException e1) {
            e1.printStackTrace();
        }

        List<Student> students = readFromJson(TempPath);
        ListToXml(students,xmlPath);

    }
}
