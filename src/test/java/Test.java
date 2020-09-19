import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import serializer.Serializer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

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
            System.out.println(JSONObject.toJSONString(object, SerializerFeature.WriteMapNullValue));
        }

        JSONObject o = new JSONObject();
        o.put("emptyShit", null);
        System.out.println(JSONObject.toJSONString(o, SerializerFeature.WriteMapNullValue));

    }
}
