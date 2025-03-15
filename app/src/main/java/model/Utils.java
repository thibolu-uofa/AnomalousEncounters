package model;

import android.content.Context;
import org.json.JSONArray;
import org.json.JSONException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public final class Utils {

    /**
     * Loads a JSON array from a file in the assets directory.
     *
     * @param filename The name of the file to load from assets
     * @param context The Android context used to access assets
     * @return A JSONArray containing the parsed contents of the file
     * @throws RuntimeException If file cannot be read or parsed
     */
    public static JSONArray loadJsonArrayFromFile(String filename, Context context) {
        try (InputStream inputStream = context.getAssets().open(filename);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            StringBuilder skillString = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                skillString.append(line);
            }

            reader.close();
            inputStream.close();
            return new JSONArray(skillString.toString());
        } catch (JSONException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves and concatenates specific property values from a JSON array file.
     *
     * @param filename The name of the JSON file in assets
     * @param property The JSON property name to extract
     * @param ids Array of indices identifying the JSON objects to access
     * @param context The Android context used to access assets
     * @return A string containing all property values concatenated with newlines
     * @throws RuntimeException If property cannot be accessed or parsed
     */
    public static String getDataProperty(String filename, String property, int[] ids, Context context) {
        StringBuilder dataProperties = new StringBuilder();
        JSONArray jsonArray = loadJsonArrayFromFile(filename, context);
        for (int id: ids) {
            try {
                dataProperties.append(jsonArray.getJSONObject(id).getString(property)).append("\n");
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        return String.valueOf(dataProperties);
    }

    /**
     * Retrieves a list of property values from a JSON array file.
     *
     * @param filename The name of the JSON file in assets
     * @param property The JSON property name to extract
     * @param ids Array of indices identifying the JSON objects to access
     * @param context The Android context used to access assets
     * @return An ArrayList containing the requested property values
     * @throws RuntimeException If property cannot be accessed or parsed
     */
    public static ArrayList<String> getListOfDataProperty(String filename, String property, int[] ids, Context context) {
        ArrayList<String> dataProperties = new ArrayList<String>();
        JSONArray jsonArray = loadJsonArrayFromFile(filename, context);
        for (int id: ids) {
            try {
                String dataProperty = jsonArray.getJSONObject(id).getString(property);
                dataProperties.add(dataProperty);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        return dataProperties;
    }
}