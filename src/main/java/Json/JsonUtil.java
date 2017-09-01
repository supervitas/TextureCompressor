package Json;

import org.json.JSONException;
import org.json.JSONObject;

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
}