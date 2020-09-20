import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import dmpUtil.DMPUtil;
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

        System.out.println("LoadRenameFile");
        System.out.println("===========================================");
        serializer.loadRenameMappingFromFile(new File("src/test/resources/rename_mapping_1.txt"));

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
            String serializedObject = serializer.flatten(object);
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
            JSONObject object = serializer.reform(rawFileContent);
            System.out.println(JSONObject.toJSONString(object, SerializerFeature.WriteMapNullValue));
        }

        System.out.println("DMPUtil.GenerateDMPInput");
        System.out.println("===========================================");

        String[] baseLineFiles = new String[] {
                "src/test/resources/DMP/baseline1.json;src/test/resources/DMP/modification1.txt",
        };

        for (String filePair: baseLineFiles) {
            String[] baselineAndModification = filePair.split(";");
            String baselineFile = baselineAndModification[0];
            String modificationFile = baselineAndModification[1];
            JSONObject baseline = serializer.loadJsonFromFile(new File(baselineFile));
            JSONObject modification = serializer.reform(
                    new Scanner(new File(modificationFile)).useDelimiter("\\Z").next());
            System.out.println(JSONObject.toJSONString(baseline));
            System.out.println(JSONObject.toJSONString(modification));
            DMPUtil dmpUtil = new DMPUtil();
            JSONObject generatedInput = dmpUtil.GenerateDMPInput(baseline, modification);
            System.out.println(JSONObject.toJSONString(generatedInput, SerializerFeature.PrettyFormat));
        }

    }
}
