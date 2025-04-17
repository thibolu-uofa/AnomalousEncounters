package model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
            Log.e("Error with JSON file", "failed to load JSON files", e);
            throw new RuntimeException(e);
        }
    }

    public static JSONArray loadJsonArrayFromFileOnDevice(String filename, Context context) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(context.openFileInput(filename)))) {

            StringBuilder skillString = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                skillString.append(line);
            }

            return new JSONArray(skillString.toString());

        } catch (FileNotFoundException e) {
            Log.e("Error with JSON file", "JSON file does not exist", e);
            return new JSONArray();
        } catch (JSONException | IOException e) {
            Log.e("Error with JSON file", "failed to load JSON files", e);
            throw new RuntimeException(e);
        }
    }


    public static void saveJSONArrayOnUserDevice(JSONArray jsonArray, Context context) {
        String filename = "save_slots.json";
        try (FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE)) {
            fos.write(jsonArray.toString().getBytes());
            fos.flush();
        } catch (Exception e) {
            Log.e("Error with JSON file", "failed to save JSON files", e);
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
                Log.e("Error with JSON file", "failed to load property from JSON file", e);
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
    public static ArrayList<String> getStringListOfDataProperty(String filename, String property, int[] ids, Context context) {
        ArrayList<String> dataProperties = new ArrayList<String>();
        JSONArray jsonArray = loadJsonArrayFromFile(filename, context);
        for (int id: ids) {
            try {
                String dataProperty = jsonArray.getJSONObject(id).getString(property);
                dataProperties.add(dataProperty);
            } catch (JSONException e) {
                Log.e("Error with JSON file", "failed to load JSON files", e);
                throw new RuntimeException(e);
            }
        }
        return dataProperties;
    }


    public static Object getSingleDataProperty(String filename, String property, int id, Context context) {
        Object dataProperty;
        JSONArray jsonArray = loadJsonArrayFromFile(filename, context);
        try {
             dataProperty = jsonArray.getJSONObject(id).get(property);
        } catch (JSONException e) {
            Log.e("Error with JSON file", "failed to load JSON file property", e);
            throw new RuntimeException(e);
        }
        return dataProperty;
    }

    public static Object getSingleDataPropertyFromJSONArray(JSONArray jsonArray, String property, int id, Context context) {
        Object dataProperty;
        try {
            dataProperty = jsonArray.getJSONObject(id).get(property);
        } catch (JSONException e) {
            Log.e("Error with JSON file", "failed to load JSON file property", e);
            throw new RuntimeException(e);
        }
        return dataProperty;
    }

    public static Object getPropertyByName(String filename, String name, String property, Context context) {
        JSONArray jsonArray = loadJsonArrayFromFile(filename, context);
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                String itemName = object.getString("name");
                if (itemName.equals(name)) {
                    return object.get(property);
                }

            }
        } catch (JSONException e) {
            Log.e("Error with JSON file", "failed to load property from JSON file", e);
            throw new RuntimeException(e);
        }

        return "";
    }

    public static Bitmap getEnemyImage(int id, Context context) {
        String imageName = (String) getSingleDataProperty("enemies.json", "image_name", id, context);
        int resourceId = context.getResources().getIdentifier(imageName, "drawable",  context.getPackageName());
        return BitmapFactory.decodeResource(context.getResources(), resourceId);
    }

    /**
     * Gets the tiles of an attack type, in relation to an origin position.
     *
     * @param origin_pos The starting position [row, col] for the pattern
     * @param distance The number of steps to take in each direction
     * @param positionVectors Array of direction vectors to extend from origin
     * @return ArrayList of valid board positions [row, col] that form the pattern
     */
    public static ArrayList<int[]> getRepeatingPattern(int[] origin_pos, int distance, int[][] positionVectors, int maxRows, int maxCols) {
        ArrayList<int[]> attackPattern = new ArrayList<int[]>();

        for (int[] vector: positionVectors) {
            int[] currentTile = origin_pos;

            for (int i = 0; i < distance; i++) {
                int[] newTile = new int[2];
                newTile[0] = currentTile[0] + vector[0];
                newTile[1] = currentTile[1] + vector[1];
                if (!(newTile[0] < 0 || newTile[0] >= maxCols || newTile[1] < 0 || newTile[1] >= maxRows)) {
                    attackPattern.add(newTile);
                }
                currentTile = newTile;
            }
        }
        return attackPattern;
    }

    public static boolean hasPlayerProgressedPhase(PlayerState playerState) {
        int phase = playerState.getPhase();
        int totalSkillLevel = playerState.getTotalSkillLevel();
        int numberOfSkills = playerState.getSkills().size();
        float avgSkillLevel = (float)totalSkillLevel/(float) numberOfSkills;

        // phase progresses from phase 1 to phase 2 when the avg skill level is >= 2.6
        if (phase == 1 && avgSkillLevel >= 2.6) {
            return true;
        }

        // phase progresses from phase 2 to phase 3 when the avg skill level is >= 5
        if (phase == 2 && avgSkillLevel >= 5) {
            return true;
        }

        return false;
    }
}