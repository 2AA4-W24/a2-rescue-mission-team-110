package ca.mcmaster.se2aa4.island.team110;

import ca.mcmaster.se2aa4.island.team110.Interfaces.Translator;
import ca.mcmaster.se2aa4.island.team110.Models.Information;
import ca.mcmaster.se2aa4.island.team110.Models.Mesh;

import org.json.JSONObject;
import org.json.JSONTokener;
import java.io.FileReader;
import java.io.IOException;
import org.json.JSONArray;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class JsonTranslator implements Translator {
    private static final Logger logger = LogManager.getLogger(JsonTranslator.class);

    @Override
    public Information translateJson(String filename) {
      Information info = new Information();

        try (FileReader reader = new FileReader(filename)) {
            JSONObject jsonObject = new JSONObject(new JSONTokener(reader));
            info.setEdgeProps(parseProps(jsonObject.getJSONArray("edge_props")));
            info.setVertexProps(parseProps(jsonObject.getJSONArray("vertex_props")));
            info.setFaceProps(parseProps(jsonObject.getJSONArray("face_props")));
            info.setMesh(parseMesh(jsonObject.getJSONObject("mesh")));    

        } catch (IOException e) {
            logger.error("Error reading file: " + filename, e);
        }
        return info;
    }

    private Map<Integer, Map<String, Object>> parseProps(JSONArray array) {
        Map<Integer, Map<String, Object>> map = new HashMap<>();
        
            for (int i = 0; i< array.length(); i++){
                JSONObject prop = array.getJSONObject(i);
                int key = prop.getInt("key");
                JSONArray vals = prop.getJSONArray("vals");

                Map<String,Object> information = map.getOrDefault(key, new HashMap<>());
                for (int j=0; j<vals.length(); j++){
                    JSONObject val = vals.getJSONObject(j);
                    String info = val.getString("p");
                    Object value = val.get("v");
                    information.put(info, value);
                } 

                map.put(key, information);
            }



        return map;
    }

    private Mesh parseMesh(JSONObject meshObject) {
      Mesh mesh = new Mesh();
      // Parse size, vertices, edges, and faces
      return mesh;
  }
}
