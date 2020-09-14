package serializer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;

import org.json.JSONArray;
import org.json.JSONObject;

public class Serializer {
    public Serializer() {}

    public String serialize(JSONObject object) {
        return this.serialize(object, "");
    }

    public String serialize(JSONObject object, String parent) {
        String result = "";
        for (String key: object.keySet()) {
            if (object.get(key) instanceof JSONArray) {
                result += this.serialize((JSONArray) object.get(key), parent=="" ? key:parent+"."+key);
            } else if (object.get(key) instanceof JSONObject) {
                result += this.serialize((JSONObject) object.get(key), parent=="" ? key:parent+"."+key);
            } else {
                if (parent != "") {
                    result += parent;
                    result += ".";
                }
                result += key;
                result += "=";
//                last layer of key value pair
                if (object.get(key) instanceof String) {
                    result += "\"";
                    result += object.get(key);
                    result += "\"";
                } else {
                    result += object.get(key);
                }
                result += ";\n";
            }
        }
        return result;
    }

    public String serialize(JSONArray array) {
        return this.serialize(array, "");
    }

    public String serialize(JSONArray arr, String parent) {
        String result = "";
        for (int i=0; i<arr.length();i++) {
            if (arr.get(i) instanceof JSONObject) {
                result += this.serialize((JSONObject) arr.get(i), parent+"#"+i);
            } else if (arr.get(i) instanceof JSONArray) {
                result += this.serialize((JSONArray) arr.get(i), parent+"#"+i);
            } else {
                result += parent;
                result += "#";
                result += i;
                result += "=";
                result += arr.get(i);
                result += ";\n";
            }
        }
        return result;
    }


    public String deserialize(String string) {
        return "fuck this";
    }

    public JSONObject loadJsonFromFile(File file) throws IOException {
        String content = FileUtils.readFileToString(file, "UTF-8");
        return this.loadJsonFromString(content);
    }

    public JSONObject loadJsonFromString(String s) {
        return new JSONObject(s);
    }
}
