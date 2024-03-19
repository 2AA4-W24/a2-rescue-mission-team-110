package ca.mcmaster.se2aa4.island.team110.Aerial;

import org.json.JSONObject;
import ca.mcmaster.se2aa4.island.team110.Interfaces.Controller;

public class DroneController implements Controller {
    @Override
    public String fly() {
        JSONObject decision = new JSONObject();
        decision.put("action", "fly");
        return decision.toString();
    }
    
    @Override
    public String turn(String direction) {
        JSONObject decision = new JSONObject();
        decision.put("action", "heading");
        decision.put("parameters", new JSONObject().put("direction", direction)); 
        return decision.toString();
    }

    @Override
    public String stop() {
        JSONObject decision = new JSONObject();
        decision.put("action", "stop");
        return decision.toString();
    }
}
