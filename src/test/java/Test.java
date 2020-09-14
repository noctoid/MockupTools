import org.json.JSONArray;
import org.json.JSONObject;
import serializer.Serializer;
import NewWorld.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

        NewWorld.Serializer hot_shit = new NewWorld.Serializer();
        hot_shit.deserialize("stringValue=\"this is a string\";\n" +
                "array_1#0=1;\n" +
                "array_1#1=2;\n" +
                "array_1#2=3;");

    }
}
