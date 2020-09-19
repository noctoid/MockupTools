import org.json.JSONArray;
import org.json.JSONObject;
import serializer.Serializer;
import NewWorld.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import comparison.Comparison;

public class Test {

    public static void main(String[] args) throws IOException {
        System.out.println("Testing Serializer Methods");
        System.out.println("===========================================");

        System.out.println("Constructor");
        System.out.println("===========================================");
        Serializer serializer = new Serializer();

        System.out.println("loadJsonFromFile");
        System.out.println("===========================================");
        String[] testJsonFiles = new String[]{
                "/home/yoko/workspace/MockupTools/src/test/resources/test-1.json",
                "/home/yoko/workspace/MockupTools/src/test/resources/test-2.json"
        };
        ArrayList<JSONObject> testJsons = new ArrayList<>();
        for (String testJsonFile : testJsonFiles) {
            JSONObject object = serializer.loadJsonFromFile(new File(testJsonFile));
            testJsons.add(object);
        }

        System.out.println("serialize");
        System.out.println("===========================================");

        for (JSONObject object: testJsons) {
            String serializedObject = serializer.serialize(object);
            System.out.println(serializedObject);
        }

        System.out.println("deserialize");
        System.out.println("===========================================");

        String[] testTxtFiles = new String[] {
                "/home/yoko/workspace/MockupTools/src/test/resources/test-0.txt",
                "/home/yoko/workspace/MockupTools/src/test/resources/test-1.txt"
        };

        for (String txtFile: testTxtFiles) {
            File rawFile = new File(txtFile);
            String rawFileContent = new Scanner(rawFile).useDelimiter("\\Z").next();
            JSONObject object = serializer.deserialize(rawFileContent);
            System.out.println(object.toString(2));
        }


        JSONObject o = new JSONObject();
        JSONObject p1 = new JSONObject();
        JSONObject p2 = new JSONObject();
        JSONArray a1 = new JSONArray();
        JSONArray a2 = new JSONArray();
        a1.put(1);
        a2.put(2);

        o.accumulate("a", a1);
        o.accumulate("a", a2);

//        o.put("shit", "isReal");
//        o.accumulate("shit", "shit hits the fan");
        p1.put("p1", 1);
        p2.put("p2", 2);

//        o.put("o", p1);
        for (String key : p2.keySet()) {
            o.put(key, p2.get(key));
        }
        for (String key : p1.keySet()) {
            o.put(key, p1.get(key));
        }
        System.out.println(o.toString(2));
        System.out.println(o.keySet());


//        NewWorld.Serializer hot_shit = new NewWorld.Serializer();
//        hot_shit.deserialize("stringValue=\"this is a string\";\n" +
//                "array_1#0=1;\n" +
//                "array_1#1=2;\n" +
//                "array_1#2=3;");

    }
}
