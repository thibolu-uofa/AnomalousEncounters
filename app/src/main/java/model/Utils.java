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
