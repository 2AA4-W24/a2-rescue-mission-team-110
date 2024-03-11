package ca.mcmaster.se2aa4.island.team110;

import org.json.JSONObject;
import ca.mcmaster.se2aa4.island.team110.Interfaces.Radar;

public class DroneRadar implements Radar {
    @Override
    public String echo(String direction) {
        JSONObject decision = new JSONObject();
        decision.put("action", "echo");
        decision.put("parameters", new JSONObject().put("direction", direction)); // direction should be enum of N,S,E or W change this
        return decision.toString();
    }

    public String echoMultiDirections() {
      String[] directions = {"N", "S", "E", "W"};
      for (int i=0; i<directions.length; i++){
        String direction = directions[i];
        String response = echo(direction);
        JSONObject jsonResponse = new JSONObject(response);

        if (jsonResponse.getJSONObject("extras").getString("found").equals("GROUND")){
          return direction;
        }
          
        
      } return "NONE";
    }
}