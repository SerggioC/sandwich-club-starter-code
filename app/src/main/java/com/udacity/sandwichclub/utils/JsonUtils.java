package com.udacity.sandwichclub.utils;

import android.util.JsonReader;

import com.udacity.sandwichclub.model.Sandwich;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class JsonUtils {

    public static Sandwich parseSandwichJson(String json) {
        String mainName;
        List<String> alsoKnownAs;
        String placeOfOrigin;
        String description;
        String image;
        List<String> ingredients;

        try {
            JSONObject data = new JSONObject(json);
            JSONObject name = data.optJSONObject("name");
            mainName = name == null ? null : name.getString("mainName");
            JSONArray akaJSONArray = name.optJSONArray("alsoKnownAs");
            alsoKnownAs = akaJSONArray == null ? null : getListFromJsonArray(akaJSONArray);
            placeOfOrigin = data.optString("placeOfOrigin");
            description = data.optString("description");
            image = data.optString("image");
            JSONArray ingredientsJSONArray = data.optJSONArray("ingredients");
            ingredients = ingredientsJSONArray == null ? null : getListFromJsonArray(ingredientsJSONArray);

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return new Sandwich(mainName, alsoKnownAs, placeOfOrigin, description, image, ingredients);

    }

    private static List<String> getListFromJsonArray(JSONArray jsonArray) throws JSONException {
        int capacity = jsonArray.length();
        List<String> list = new ArrayList<>(capacity);
        for (int i = 0; i < capacity; i++) {
            list.add(jsonArray.getString(i));
        }
        return list;
    }


    // Example data
//          {"name": {"mainName":"Ham and cheese sandwich","alsoKnownAs":[]},
//           "placeOfOrigin":"",
//           "description":"A ham and cheese
//           sandwich is a common type of sandwich. It is made by putting cheese and sliced ham
//           between two slices of bread. The bread is sometimes buttered and/or toasted. Vegetables
//           like lettuce, tomato, onion or pickle slices can also be included. Various kinds of
//           mustard and mayonnaise are also common.",
//           "image":"https://upload.wikimedia.org/wikipedia/commons/thumb/5/50/Grilled_ham_and_cheese_014.JPG/800px-Grilled_ham_and_cheese_014.JPG",
//           "ingredients":["Sliced bread","Cheese","Ham"]}


    // Alternative method using android.util.JsonReader
    public static Sandwich parseSandwichJsonAlternative(String json) {
        JsonReader reader = null;
        try {
            InputStream in = new ByteArrayInputStream(json.getBytes("UTF-8"));
            reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (reader == null) return null;

        String mainName = "";
        List<String> alsoKnownAs = new ArrayList<>();
        String placeOfOrigin = "";
        String description = "";
        String image = "";
        List<String> ingredients = new ArrayList<>();


        try {
            reader.beginObject();

            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("name")) {
                    reader.beginObject();
                    while (reader.hasNext()) {
                        String nextName = reader.nextName();
                        if (nextName.equals("mainName")) {
                            mainName = reader.nextString();
                        } else if (nextName.equals("alsoKnownAs")) {
                            reader.beginArray();
                            while (reader.hasNext()) {
                                alsoKnownAs.add(reader.nextString());
                            }
                            reader.endArray();
                        }
                    }
                    reader.endObject();
                } else if (name.equals("placeOfOrigin")) {
                    placeOfOrigin = reader.nextString();
                } else if (name.equals("description")) {
                    description = reader.nextString();
                } else if (name.equals("image")) {
                    image = reader.nextString();
                } else if (name.equals("ingredients")) {
                    reader.beginArray();
                    while (reader.hasNext()) {
                        ingredients.add(reader.nextString());
                    }
                    reader.endArray();
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
        } catch (IllegalStateException e1) {
            e1.printStackTrace();
            return null;
        } catch (IOException e2) {
            e2.printStackTrace();
            return null;
        } finally {
            try {
                reader.close();
            } catch (IOException e3) {
                e3.printStackTrace();
            }
        }

        return new Sandwich(mainName, alsoKnownAs, placeOfOrigin, description, image, ingredients);
    }


}
