import org.json.JSONObject;
import org.json.XML;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class XmlToJson {
    public static void xmlToJson(String xmlPath, String jsonPath) throws Exception {
        ListToJson(readFromXml(xmlPath),jsonPath);
    }

    private static void ListToJson(List<Student> students, String jsonPath) throws Exception {
        String TempFile = "D:\\temp.xml";

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("students");
        doc.appendChild(rootElement);

        for(int i = 0; i<students.size();i++){
            Element studentElem = doc.createElement("student");
            rootElement.appendChild(studentElem);
            studentElem.setAttribute("full_name", students.get(i).full_name);
            studentElem.setAttribute("id", students.get(i).id);
            studentElem.setAttribute("educational_institution", students.get(i).educational_institution);
            Element gradesElem = doc.createElement("grades");
            studentElem.appendChild(gradesElem);
            for(int j=0;j<students.get(i).grade.size();j++){
                Element gradeElem = doc.createElement("grade");
                gradesElem.appendChild(gradeElem);
                gradeElem.setAttribute("subject",students.get(i).subject.get(j));
                gradeElem.setTextContent(students.get(i).grade.get(j));
            }
        }

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new FileOutputStream(TempFile));

        transformer.transform(source, result);


        String line="",str="";
        BufferedReader br = new BufferedReader(new FileReader(TempFile));
        while ((line = br.readLine()) != null)
        {
            str+=line;
        }
        JSONObject jsondata = XML.toJSONObject(str);
        FileWriter file = new FileWriter(jsonPath);
        file.write(jsondata.toString());
        file.close();
    }

    private static List<Student> readFromXml(String xmlPath) {
        List<Student> students = new ArrayList<>();

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {

            dbf.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new File(xmlPath));
            doc.getDocumentElement().normalize();

            NodeList institutionList = doc.getElementsByTagName("educational_institution");
            for (int temp = 0; temp < institutionList.getLength(); temp++) {

                Node institution = institutionList.item(temp);
                if (institution.getNodeType() == Node.ELEMENT_NODE) {
                    Element institutionElem = (Element) institution;
                    NodeList studentList = institutionElem.getElementsByTagName("student");
                    for (int std = 0; std < studentList.getLength(); std++){
                        Node studentNode = studentList.item(std);
                        if (studentNode.getNodeType() == Node.ELEMENT_NODE){
                            Element studentElem = (Element) studentNode;

                            String full_name = studentElem.getElementsByTagName("full_name").item(0).getTextContent();
                            String id = studentElem.getAttribute("id");
                            String educational_institution = institutionElem.getAttribute("title");
                            Student tempStudent = new Student(full_name,id,educational_institution);

                            NodeList gradeList = studentElem.getElementsByTagName("grade");
                            for (int grd = 0; grd < gradeList.getLength(); grd++){
                                Node gradeNode = gradeList.item(grd);
                                if (gradeNode.getNodeType() == Node.ELEMENT_NODE){
                                    Element gradeElem = (Element) gradeNode;
                                    tempStudent.addGrade(gradeElem.getAttribute("subject"),gradeElem.getTextContent());
                                }
                            }

                            students.add(tempStudent);
                        }
                    }

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
}
