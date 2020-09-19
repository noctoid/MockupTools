package dmpUtil;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class DMPUtil {
    public JSONObject GenerateDMPInput(JSONObject baseline, JSONObject modification) {
        // change the baseline file regarding the modification file
        this.changeDMPInputElement(baseline, modification);
        return baseline;
    }

    public void changeDMPInputElement(JSONObject baseline, JSONObject modification) {
        for (String k : modification.keySet()) {
            if (baseline.get(k) instanceof JSONArray) {
                this.changeDMPInputElement((JSONArray) baseline.get(k), (JSONObject) modification.get(k));
            } else if (!(baseline.get(k) instanceof JSONArray) && baseline.get(k) instanceof JSONObject) {
                this.changeDMPInputElement((JSONObject) baseline.get(k), (JSONObject) modification.get(k));
            } else {
                baseline.put(k, modification.get(k));
            }
        }
    }

    public void changeDMPInputElement(JSONArray baseline, JSONObject modification) {
        System.out.println(baseline);
        System.out.println(modification);
        for (String k: modification.keySet()) {
            if (baseline.get(Integer.parseInt(k)) instanceof JSONArray) {
                // 开始Array套娃
                this.changeDMPInputElement(
                        (JSONArray) baseline.get(Integer.parseInt(k)),
                        (JSONObject) modification.get(k));
            } else if (baseline.get(Integer.parseInt(k)) instanceof JSONObject) {
                // 开始Object套娃
                this.changeDMPInputElement(
                        (JSONObject) baseline.get(Integer.parseInt(k)),
                        (JSONObject) modification.get(k));
            } else {
                baseline.set(Integer.parseInt(k), modification.get(k));
            }
        }
    }
}

