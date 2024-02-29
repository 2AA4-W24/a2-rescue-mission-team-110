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
  public Map<Integer, Map<String, Integer>> translateJSon(String filename) {
    Map<Integer, Map<String, Integer>> map = new HashMap<>();

    try {
      File file = new File(filename);
      FileReader reader = new FileReader(file);
      JSONTokener tokener = new JSONTokener(reader);

      JSONObject jsonObject = new JSONObject(tokener);
      JSONArray edges = jsonObject.getJSONArray("edge_props");

      for (int i = 0; i< edges.length(); i++){
        JSONObject edge = edges.getJSONObject(i);
        int key = edge.getInt("key");
        JSONArray vals = edge.getJSONArray("vals");

        Map<String,Integer> information = new HashMap<>();
        for (int j=0; j<vals.length(); j++){
          JSONObject val = vals.getJSONObject(j);
          String info = val.getString("p");
          int value = val.getInt("v");
          information.put(info, value);
        } 
        
        map.put(key, information);


      }

      
    } catch (IOException e) {
      logger.info(filename + " not accepted.");
    }

    return map;
  }

  
}
