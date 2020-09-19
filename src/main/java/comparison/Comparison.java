package comparison;

import com.alibaba.fastjson.JSONObject;

public class Comparison {
    private JSONObject baseline;
    private JSONObject modification;
    private JSONObject expectResult;
    private JSONObject actualResult;

    public Comparison() {}

    public void LoadBaseline(JSONObject baseline) {
        this.baseline = baseline;
    }

    public void LoadModification(JSONObject modification) {
        this.modification = modification;
    }

    public void LoadExpectResult(JSONObject expectResult){
        this.expectResult = expectResult;
    }

    public void LoadActualResult(JSONObject actualResult){
        this.actualResult = actualResult;
    }

}

