package dev.arun.ecomuserservice;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class JsonFileReader {
    public static void main(String[] args) {
        // Specify the URL of the JSON file
        String jsonUrl = "https://git.toptal.com/screeners/invoice-json/-/raw/main/invoice.json";

        try {
            // Open a connection to the URL
            URL url = new URL(jsonUrl);
            URLConnection connection = url.openConnection();

            // Read the JSON content from the URL
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }
            reader.close();

            // Parse the JSON content as a JSON array
            JsonParser parser = new JsonParser();
            JsonArray jsonArray = parser.parse(jsonContent.toString()).getAsJsonArray();

            // Convert the JsonArray to a Java List
            List<JsonElement> jsonList = jsonArrayToList(jsonArray);

            // Sort the list by order name in alphabetical order
            jsonList.sort(Comparator.comparing(o -> o.getAsJsonObject().get("customer_name").getAsString()));

            // Print the sorted JSON array
            System.out.println("Sorted JSON array by order name in alphabetical order:");
            jsonList.forEach(jsonElement -> System.out.println(jsonElement.toString()));


            // Sort the list by order ID in descending order
            jsonList.sort(Comparator.comparingInt(o -> -o.getAsJsonObject().get("order_id").getAsInt()));

            // Print the sorted JSON array
            System.out.println("Sorted JSON array by order ID in descending order:");
            jsonList.forEach(jsonElement -> System.out.println(jsonElement.toString()));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<JsonElement> jsonArrayToList(JsonArray jsonArray) {
        return StreamSupport.stream(jsonArray.spliterator(), false)
                .collect(Collectors.toList());
    }

}


