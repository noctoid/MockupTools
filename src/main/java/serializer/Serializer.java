package serializer;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import org.apache.commons.io.FileUtils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Serializer {
    private ScriptEngineManager scriptEngineManager;
    private ScriptEngine scriptEngine;

    private HashMap<String, String> renameMapping = new HashMap<>();

    public Serializer() {
    }

    // flatten is the method to "flatten" a multi-layer json into specialized one-layer
    // key-value pair format
    public String flatten(JSONObject object) {
        return this.flatten(object, "");
    }

    public String flatten(JSONObject object, String parent) {
        StringBuilder result = new StringBuilder();
        for (String key : object.keySet()) {
            String completeKey = parent.equals("") ? key : parent + "." + key;
            if (object.get(key) instanceof JSONArray) {
                result.append(this.flatten((JSONArray) object.get(key), completeKey));
            } else if (object.get(key) instanceof JSONObject) {
                result.append(this.flatten((JSONObject) object.get(key), completeKey));
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

    public String flatten(JSONArray array) {
        return this.flatten(array, "");
    }

    public String flatten(JSONArray arr, String parent) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < arr.size(); i++) {
            if (arr.get(i) instanceof JSONObject) {
                result.append(this.flatten((JSONObject) arr.get(i), parent + "#" + i));
            } else if (arr.get(i) instanceof JSONArray) {
                result.append(this.flatten((JSONArray) arr.get(i), parent + "#" + i));
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

    // reform is the way of reform the structure previously flattened
    public JSONObject reform(String rawFileContent) {
        JSONObject result = new JSONObject();
        // TODO: parse
        rawFileContent = this.strip(this.removeNeglected(rawFileContent));
        System.out.println(rawFileContent);
        String[] lines = rawFileContent.split(";");
        // Process content line by line
        for (String line : lines) {
            // remove white space wrapping the line
            line = this.strip(line);
            try {
                JSONObject jsonObject = this.buildObject(this.applyRenameMapping(line));
                mergeObject(result, jsonObject);
            } catch (ScriptException e) {
                e.printStackTrace();
                System.out.println("Some thing is wrong on this line");
                System.out.println(line);
            }
        }

        return result;
    }

    // feed one line of flattened json, produce one json element
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
            jsonObject.put(
                    this.strip(line.substring(0, indexOfEqual)),
                    this.javaEval(line.substring(indexOfEqual + 1)));
            return jsonObject;
        } else {
            if (indexOfDot < indexOfPound) {
                // . is the first thing found, generate
                String key = this.strip(line.substring(0, indexOfDot));
                String restOfLine = line.substring(indexOfDot+1);

                jsonObject.put(key, this.buildObject(restOfLine));
            } else {
                // # is the first thing found, array
                String keyOfArray = this.strip(line.substring(0, indexOfPound));
                String restOfLine = line.substring((indexOfPound+1));
                jsonObject.put(keyOfArray, this.buildObject(restOfLine));
            }
        }

        return jsonObject;
    }

    // merge objects produced by buildObject
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


    private String removeNeglected(String mockup) {
        String pattern = "[(].+?[)]";
        return Pattern.compile(pattern, Pattern.MULTILINE | Pattern.DOTALL).matcher(mockup).replaceAll("");
    }


    private String strip(String string) {
        String leading = "^\\s+";
        String trailing = "\\s+$";
        return Pattern.compile(trailing).matcher(
                Pattern.compile(leading).matcher(string).replaceAll("")).replaceAll("");
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
        return JSONObject.parseObject(s, Feature.OrderedField);
    }

    public void loadRenameMappingFromFile(File file) {
        try {
            String fileContent = new Scanner(file).useDelimiter("\\Z").next();
            this.loadRenameMappingFromString(fileContent);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void loadRenameMappingFromString(String s) {
        String[] fileLines = s.split(";");
        for (String fileLine: fileLines) {
            fileLine = this.strip(fileLine);
            String[] twoNames = fileLine.split("=");
            String longName = this.strip(twoNames[1]);
            String shortName = this.strip(twoNames[0]);
            this.renameMapping.put(shortName, longName);
        }
        System.out.println(this.renameMapping);
    }

    private String applyRenameMapping(String line) {
        for (String shortName: this.renameMapping.keySet()) {
            if (line.contains(shortName)) {
                line = line.replace(shortName, this.renameMapping.get(shortName));
            }
        }
        return line;
    }
}
