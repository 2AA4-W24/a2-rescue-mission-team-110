package ca.mcmaster.se2aa4.island.team110;

import ca.mcmaster.se2aa4.island.team110.Interfaces.Translator;

import org.json.JSONObject;
import org.json.JSONTokener;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.json.JSONArray;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class jsonTranslator implements Translator{
  private static final Logger logger = LogManager.getLogger(jsonTranslator.class);

  @Override
  public Map<Integer, Map<String, Object>> translateJSon(String filename) {
    Map<Integer, Map<String, Object>> map = new HashMap<>();

    try {
      File file = new File(filename);
      FileReader reader = new FileReader(file);

      JSONTokener tokener = new JSONTokener(reader);
      JSONObject jsonObject = new JSONObject(tokener);


      JSONArray edges = jsonObject.getJSONArray("edge_props");
      addToMap(edges, map);

      JSONArray vertices = jsonObject.getJSONArray("vertex_props");
      addToMap(vertices, map);

      

    

      
    } catch (IOException e) {
      logger.info(filename + " not accepted.");
    }

    

    return map;
  }

  private void addToMap(JSONArray array, Map<Integer, Map<String, Object>> map){
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
  }

  
}
