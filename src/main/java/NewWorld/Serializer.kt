package NewWorld

import org.json.JSONObject

class Serializer {
    fun deserialize(s: String): JSONObject{
        var result = JSONObject()
        var lines = s.split(";")
        for (line in lines) {
            print(line)
        }
        return result
    }
}