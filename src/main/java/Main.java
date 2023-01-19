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
        String fileCsvName = "data.csv";
        String csvToJsonFileName = "data.json";

        String fileXmlName = "data.xml";
        String xmlToJsonFileName = "data2.json";

        List<Employee> listCsv = parseCSV(columnMapping, fileCsvName);
        String json = listToJson(listCsv);
        writeString(json, csvToJsonFileName);

        List<Employee> listXml = parserXML(fileXmlName);
        String json1 = listToJson(listXml);
        writeString(json1, xmlToJsonFileName);
    }

    public static List<Employee> parseCSV(String[] columnMapping, String fileCsvName) {
        try (CSVReader csvReader = new CSVReader(new FileReader(fileCsvName))) {
            ColumnPositionMappingStrategy<Employee> strategy =
                    new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csvToBean =
                    new CsvToBeanBuilder<Employee>(csvReader)
                            .withMappingStrategy(strategy)
                            .build();
            return csvToBean.parse();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static  List<Employee> parserXML(String fileName) {
        List<Employee> employees = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(fileName));

            Element node = doc.getDocumentElement();
            NodeList nodeList = node.getElementsByTagName("employee");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node_ = nodeList.item(i);
                if (Node.ELEMENT_NODE == node_.getNodeType()) {
                    Element element = (Element) node_;
                    long id = Long.parseLong(element.getElementsByTagName("id").item(0).getTextContent());
                    String firstName = element.getElementsByTagName("firstName").item(0).getTextContent();
                    String lastName = element.getElementsByTagName("lastName").item(0).getTextContent();
                    String country = element.getElementsByTagName("country").item(0).getTextContent();
                    int age = Integer.parseInt(element.getElementsByTagName("age").item(0).getTextContent());
                    Employee employee = new Employee(id, firstName, lastName, country, age);
                    employees.add(employee);
                }
            }

        } catch (IOException | SAXException | ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        return employees;
    }

    public static String listToJson(List<Employee> list) {
        Gson gson = new GsonBuilder().create();
        Type listType = new TypeToken<List<Employee>>() {}.getType();
        return gson.toJson(list, listType);
    }

    public static void writeString(String json, String jsonFileName)  {
        try (FileWriter fileWriter = new FileWriter(jsonFileName)) {
            fileWriter.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}