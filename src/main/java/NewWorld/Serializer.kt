package NewWorld

import org.json.JSONObject

class Serializer {
    fun deserialize(s: String): JSONObject{
        var result = JSONObject()
        var lines = s.split(";")
        for (line in lines) {
            var tokens = line.split("=")
            print(line.trimMargin())
            println(tokens)
        }
        return result
    }
}