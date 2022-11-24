import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("Введите путь исходного файла и путь итогового файла в двух отдельных строках");

        Scanner in = new Scanner(System.in);
        String input = in.nextLine();
        String output = in.nextLine();

        in.close();




        if(input.substring((input.length()-4)).equals(".xml") &&
            output.substring((output.length()-5)).equals(".json")){
            XmlToJson.xmlToJson(input,output);
            System.out.printf("Конвертация Xml в Json завершена");
        } else if (output.substring((output.length()-4)).equals(".xml") &&
                input.substring((input.length()-5)).equals(".json")) {
            JsonToXml.jsonToXml(input,output);
            System.out.printf("Конвертация Json в Xml завершена");
        }
        else {System.out.printf("Ошибка ввода");}


    }
}