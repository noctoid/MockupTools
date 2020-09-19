package serializer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class Serializer {
    private ScriptEngineManager scriptEngineManager;
    private ScriptEngine scriptEngine;

    public Serializer() {
    }

    public String serialize(JSONObject object) {
        return this.serialize(object, "");
    }

    public String serialize(JSONObject object, String parent) {
        String result = "";
        for (String key : object.keySet()) {
            if (object.get(key) instanceof JSONArray) {
                result += this.serialize((JSONArray) object.get(key), parent == "" ? key : parent + "." + key);
            } else if (object.get(key) instanceof JSONObject) {
                result += this.serialize((JSONObject) object.get(key), parent == "" ? key : parent + "." + key);
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
        for (int i = 0; i < arr.length(); i++) {
            if (arr.get(i) instanceof JSONObject) {
                result += this.serialize((JSONObject) arr.get(i), parent + "#" + i);
            } else if (arr.get(i) instanceof JSONArray) {
                result += this.serialize((JSONArray) arr.get(i), parent + "#" + i);
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


    public JSONObject deserialize(String rawFileContent) {
        JSONObject result = new JSONObject();
        // TODO: parse
        String[] lines = rawFileContent.split(";");
        // Process content line by line
        for (String line : lines) {
            // remove white space wrapping the line
            line = line.strip();
            try {
                JSONObject jsonObject = this.buildObject(line);
                System.out.println(jsonObject.toString(2));

                result = mergeObject(result, jsonObject);

            } catch (ScriptException e) {
                e.printStackTrace();
                System.out.println("Some thing is wrong on this line");
                System.out.println(line);
            }
        }

        return result;
    }

    private JSONObject buildObject(String line) throws ScriptException {
        JSONObject jsonObject = new JSONObject();
        // find first . or #
        int indexOfEqual = line.indexOf("=");
        int indexOfDot = line.indexOf(".");
        int indexOf井 = line.indexOf("#");
        indexOfDot = indexOfDot == -1 ? line.length() + 1 : indexOfDot;
        indexOf井 = indexOf井 == -1 ? line.length() + 1 : indexOf井;

        if (Math.min(indexOfEqual, Math.min(indexOfDot, indexOf井)) == indexOfEqual) {
            // there is no more . or #, simple key value pair
            jsonObject.put(
                    line.substring(0, indexOfEqual).strip(),
                    this.javaEval(line.substring(indexOfEqual + 1)));
//            System.out.println(jsonObject);
            return jsonObject;
        } else {
            if (indexOfDot < indexOf井) {
                // . is the first thing found, generate
                String key = line.substring(0, indexOfDot).strip();
                String restOfLine = line.substring(indexOfDot+1);

                jsonObject.put(key, this.buildObject(restOfLine));
            } else {
                // # is the first thing found, array
                // TODO 啊啊啊啊！！！！
                jsonObject.accumulate(line.substring(0, indexOf井).strip(), line.substring(indexOf井+1));
            }
        }

        return jsonObject;
    }

//    private JSONArray buildObject(String key, String value) {
//        return new JSONArray();
//    }

    private JSONObject mergeObject(JSONObject a, JSONObject b) {
        for (String key: b.keySet()) {
            if (!a.has(key)) {
                a.put(key, b.get(key));
            } else {
                if (b.get(key) instanceof JSONObject) {
                    a.put(key, mergeObject((JSONObject) a.get(key), (JSONObject) b.get(key)));
                } else if (b.get(key) instanceof JSONArray) {
                    // TODO Array
                } else {
                    // TODO String, Int, Float, Null
                }
            }
        }
        return a;
    }

    // do Eval operation using script engine since there is no simple eval() method in java
    private Object javaEval(String string) throws ScriptException {
        if (this.scriptEngine == null) {
            this.scriptEngineManager = new ScriptEngineManager();
            this.scriptEngine = this.scriptEngineManager.getEngineByName("js");
        }
        return scriptEngine.eval(string);
    }


    public JSONObject loadJsonFromFile(File file) throws IOException {
        String content = FileUtils.readFileToString(file, "UTF-8");
        return this.loadJsonFromString(content);
    }

    public JSONObject loadJsonFromString(String s) {
        return new JSONObject(s);
    }
}
