import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import java.io.FileReader;
import java.io.IOException;

public class CsvReader {

        public static void main(String[] args) {
            String csvFilePath = "users.csv";

            try (FileReader reader = new FileReader(csvFilePath);
                 CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

                for (CSVRecord record : csvParser) {
                    String id = record.get("id");
                    String name = record.get("name");
                    String email = record.get("email");
                    String age = record.get("age");

                    System.out.println("ID: " + id);
                    System.out.println("Name: " + name);
                    System.out.println("Email: " + email);
                    System.out.println("Age: " + age);
                    System.out.println();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

