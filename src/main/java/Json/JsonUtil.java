package Json;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class JsonUtil {
    public static String ToJson(String key, String value) {
        JSONObject json = new JSONObject();
        try {
            json.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    public static String ToJson(HashMap<String, String> values) {
        JSONObject json = new JSONObject();
        try {
            for (Map.Entry<String, String> entry : values.entrySet()) {
                json.put(entry.getKey(), entry.getValue());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }


    public static HashMap<String, String> ParseJson(String data, String [] fields) {
        HashMap<String, String> userData = new HashMap<>();
        try {
            JSONObject obj = new JSONObject(data);
            for (String field : fields) {
                userData.put(field, obj.getString(field));
            }
        } catch (JSONException e) {
            return null;
        }
        return userData;
    }
}