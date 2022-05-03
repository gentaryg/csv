import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.*;
import org.xml.sax.SAXException;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> list = parseCSV(columnMapping, fileName);
        List<Employee> list2 = parseXML("dataXML.xml");
        String json = listToJson(list2);
        writerString(json);

    }

    private static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        List<Employee> staff = null;
        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {


            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);

            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .build();
            staff = csv.parse();

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return staff;
    }
    private static <T> String listToJson (List<Employee> list) {
        Type listType = new TypeToken<List<T>>() {}.getType();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String json = gson.toJson(list, listType);
        return json;
    }
    private static void writerString (String json) {
        try (FileWriter file = new FileWriter("new_data.json")) {
            file.write(json.toString());
            file.flush();
        }catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    private static ArrayList<Employee> parseXML(String s) {
        ArrayList<Employee> list = new ArrayList<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(s));

            Node root = document.getDocumentElement();

            NodeList nodeList = root.getChildNodes();
            for (int i=0;i < nodeList.getLength();i++) {
                Node node = nodeList.item(i);
                if (Node.ELEMENT_NODE == node.getNodeType()) {
                    Element element = (Element) node;
                    long id = Long.parseLong(element.getElementsByTagName("id").item(0).getTextContent());
                    String fn = element.getElementsByTagName("firstName").item(0).getTextContent();
                    String ln = element.getElementsByTagName("lastName").item(0).getTextContent();
                    String c = element.getElementsByTagName("country").item(0).getTextContent();
                    int a = Integer.parseInt(element.getElementsByTagName("age").item(0).getTextContent());
                    Employee staff = new Employee(id,fn,ln,c,a);
                    list.add(staff);
                }
            }



        }catch (ParserConfigurationException | SAXException | IOException ex) {
            ex.printStackTrace();
        }

        return list;
    }

}
