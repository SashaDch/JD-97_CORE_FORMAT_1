import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.*;
import ru.netology.employees.Employee;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};

        List<Employee> list = parseCSV(columnMapping, "data.csv");
        String json = listToJson(list);
        writeString(json, "data.json");

        List<Employee> list2 = parseXML("data.xml");
        String json2 = listToJson(list2);
        writeString(json2, "data2.json");
    }

    private static void writeString(String str, String fileName) {
        try (FileWriter fileWriter = new FileWriter(fileName)) {
            fileWriter.write(str);
        } catch (IOException e) {
            System.err.println(e.toString());
        }
    }

    private static String listToJson(List<Employee> list) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(list, new TypeToken<List<Employee>>() {}.getType());
    }

    private static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            return csv.parse();
        } catch (IOException e) {
            System.err.println(e.toString());
        }
        return new ArrayList<Employee>();
    }

    private static List<Employee> parseXML(String fileName) {
        Document doc;
        try {
            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(fileName);
        } catch (Exception e) {
            System.err.println(e.toString());
            return new ArrayList<Employee>();
        }

        NodeList list = doc.getDocumentElement().getChildNodes();
        List<Employee> employees = new ArrayList<>(list.getLength());
        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            Element elem = (Element) node;
            employees.add(new Employee(
                    Long.parseLong(elem.getElementsByTagName("id").item(0).getTextContent()),
                    elem.getElementsByTagName("firstName").item(0).getTextContent(),
                    elem.getElementsByTagName("lastName").item(0).getTextContent(),
                    elem.getElementsByTagName("country").item(0).getTextContent(),
                    Integer.parseInt(elem.getElementsByTagName("age").item(0).getTextContent())));
        }
        return employees;
    }

}
