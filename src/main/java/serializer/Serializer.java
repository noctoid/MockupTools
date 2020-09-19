package serializer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import org.apache.commons.io.FileUtils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.IOException;

//import org.json.JSONArray;
//import org.json.JSONObject;

public class Serializer {
    private ScriptEngineManager scriptEngineManager;
    private ScriptEngine scriptEngine;

    public Serializer() {
    }

    public String serialize(JSONObject object) {
        return this.serialize(object, "");
    }

    public String serialize(JSONObject object, String parent) {
        StringBuilder result = new StringBuilder();
        for (String key : object.keySet()) {
            String completeKey = parent.equals("") ? key : parent + "." + key;
            if (object.get(key) instanceof JSONArray) {
                result.append(this.serialize((JSONArray) object.get(key), completeKey));
            } else if (object.get(key) instanceof JSONObject) {
                result.append(this.serialize((JSONObject) object.get(key), completeKey));
            } else {
                if (!parent.equals("")) {
                    result.append(parent);
                    result.append(".");
                }
                result.append(key);
                result.append("=");
//                last layer of key value pair
                if (object.get(key) instanceof String) {
                    result.append("\"");
                    result.append(object.get(key));
                    result.append("\"");
                } else if (object.get(key) == null) {
                    result.append("null");
                } else {
                    result.append(object.get(key));
                }
                result.append(";\n");
            }
        }
        return result.toString();
    }

    public String serialize(JSONArray array) {
        return this.serialize(array, "");
    }

    public String serialize(JSONArray arr, String parent) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < arr.size(); i++) {
            if (arr.get(i) instanceof JSONObject) {
                result.append(this.serialize((JSONObject) arr.get(i), parent + "#" + i));
            } else if (arr.get(i) instanceof JSONArray) {
                result.append(this.serialize((JSONArray) arr.get(i), parent + "#" + i));
            } else {
                result.append(parent);
                result.append("#");
                result.append(i);
                result.append("=");
                result.append(arr.get(i));
                result.append(";\n");
            }
        }
        return result.toString();
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
//                System.out.println(jsonObject);

                // TODO: after fastjson adaptation use putAll instead
                mergeObject(result, jsonObject);

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
        int indexOfPound = line.indexOf("#");
        indexOfDot = indexOfDot == -1 ? line.length() + 1 : indexOfDot;
        indexOfPound = indexOfPound == -1 ? line.length() + 1 : indexOfPound;

        if (Math.min(indexOfEqual, Math.min(indexOfDot, indexOfPound)) == indexOfEqual) {
            // there is no more . or #, simple key value pair
            System.out.println(this.javaEval(line.substring(indexOfEqual + 1)));
            jsonObject.put(
                    line.substring(0, indexOfEqual).strip(),
                    this.javaEval(line.substring(indexOfEqual + 1)));
//            System.out.println(jsonObject);
            return jsonObject;
        } else {
            if (indexOfDot < indexOfPound) {
                // . is the first thing found, generate
                String key = line.substring(0, indexOfDot).strip();
                String restOfLine = line.substring(indexOfDot+1);

                jsonObject.put(key, this.buildObject(restOfLine));
            } else {
                // # is the first thing found, array
                // TODO 啊啊啊啊！！！！
                System.out.println("啊啊啊啊啊啊");
                String keyOfArray = line.substring(0, indexOfPound).strip();
                String restOfLine = line.substring((indexOfPound+1));
                jsonObject.put(keyOfArray, this.buildObject(restOfLine));
            }
        }

        return jsonObject;
    }

//    private JSONArray buildObject(String key, String value) {
//        return new JSONArray();
//    }

    private JSONObject mergeObject(JSONObject a, JSONObject b) {
        for (String key: b.keySet()) {
            if (!a.containsKey(key)) {
                a.put(key, b.get(key));
            } else {
                if (b.get(key) instanceof JSONObject) {
                    a.put(key, mergeObject((JSONObject) a.get(key), (JSONObject) b.get(key)));
                } else if (b.get(key) instanceof JSONArray) {
                    // TODO Array
                    System.out.println("found array. skip for now");
                } else {
                    // TODO String, Int, Float, Null
                    System.out.println("found string/int/float/null, skip for now");
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
        return JSON.parseObject(s, Feature.OrderedField);
    }
}
