import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import dmpUtil.DMPUtil;
import serializer.Serializer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class SerializerTest {

    @Test
    void ConstructorTest() {
        Serializer serializer = new Serializer();
        assertNotEquals(serializer, null);
    }

    @Test
    void LoadFilesTest() {
        Serializer serializer = new Serializer();
        serializer.loadRenameMappingFromFile(new File("src/test/resources/rename_mapping_1.txt"));
    }

    @Test
    void FlattenTest() throws IOException {
        Serializer serializer = new Serializer();

        String[] testJsonFiles = new String[]{
            "src/test/resources/test-1.json",
            "src/test/resources/test-2.json"
        };
        ArrayList<JSONObject> testJsons = new ArrayList<>();
        for (String testJsonFile : testJsonFiles) {
            JSONObject object = serializer.loadJsonFromFile(new File(testJsonFile));
            testJsons.add(object);
        }
        for (JSONObject object: testJsons) {
            String serializedObject = serializer.flatten(object);
            System.out.println(serializedObject);
        }
    }

    @Test
    void ReformTest() {
        Serializer serializer = new Serializer();
        String[] testTxtFiles = new String[] {
            "src/test/resources/test-0.txt",
            "src/test/resources/test-1.txt"
    };

        for (String txtFile: testTxtFiles) {
            try {
                File rawFile = new File(txtFile);
                Scanner scanner = new Scanner(rawFile).useDelimiter("\\Z");
                String rawFileContent = scanner.next();
                JSONObject object = serializer.reform(rawFileContent);
                System.out.println(JSONObject.toJSONString(object, SerializerFeature.WriteMapNullValue));
                scanner.close();
            } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
    }

    @Test
    void DmpInputTest() {
        Serializer serializer = new Serializer();
        String[] baseLineFiles = new String[] {
            "src/test/resources/DMP/baseline1.json;src/test/resources/DMP/modification1.txt",
        };

        for (String filePair: baseLineFiles) {
            try {
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
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } 
        }
    }
}
