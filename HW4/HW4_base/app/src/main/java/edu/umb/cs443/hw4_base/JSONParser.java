package edu.umb.cs443.hw4_base;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nomad on 11/28/16.
 */

public class JSONParser {
    private static List<Item> items;

    public static List<Item> getData (String data) throws JSONException {

        items = new ArrayList<>();
        //  Create  JSONObject from the data
        try {
            JSONArray jArr = new JSONArray(data);
            for (int i = 0; i < jArr.length(); i++) {
                JSONObject jObj = jArr.getJSONObject(i);
                Item item = new Item();
                item.setStatus(getString("status", jObj));
                item.setTime(getLong("time", jObj));
                item.setLat(Double.parseDouble(getString("lat", jObj)));
                item.setLng(Double.parseDouble(getString("lng", jObj)));
                if(jObj.has("region")&&jObj.has("radius")) {
                    item.setRegion(getBoolean("region", jObj));
                    item.setRadius(getInt("radius", jObj));
                }
                items.add(item);
            }
            return items;
        }catch (NullPointerException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static JSONObject getObject(String tagName, JSONObject jObj)  throws JSONException {
        JSONObject subObj = jObj.getJSONObject(tagName);
        return subObj;
    }

    private static String getString(String tagName, JSONObject jObj) throws JSONException {
        return jObj.getString(tagName);
    }

    private static float  getFloat(String tagName, JSONObject jObj) throws JSONException {
        return (float) jObj.getDouble(tagName);
    }

    private static long  getLong(String tagName, JSONObject jObj) throws JSONException {
        return (long) jObj.getDouble(tagName);
    }

    private static int  getInt(String tagName, JSONObject jObj) throws JSONException {
        return jObj.getInt(tagName);
    }

    private static boolean  getBoolean(String tagName, JSONObject jObj) throws JSONException {
        return jObj.getBoolean(tagName);
    }


}
