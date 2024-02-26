package ca.mcmaster.se2aa4.island.team110;

import ca.mcmaster.se2aa4.island.team110.Interfaces.Translator;

import org.json.JSONObject;
import org.json.JSONTokener;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class jsonTranslator implements Translator{
  private static final Logger logger = LogManager.getLogger(jsonTranslator.class);

  @Override
  public JSONObject translateJSon(String filename) {
    JSONObject result = null;

    try {
      File file = new File(filename);
      FileReader reader = new FileReader(file);
      JSONTokener tokener = new JSONTokener(reader);
      result = new JSONObject(tokener);
    } catch (IOException e) {
      logger.info(filename + " not accepted.");
    }

    return result;
  }

  
}
