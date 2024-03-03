package ca.mcmaster.se2aa4.island.team110;

import org.json.JSONObject;
import ca.mcmaster.se2aa4.island.team110.interfaces.Radar;

public class DroneRadar implements Radar {
    @Override
    public String echo(String direction) {
        JSONObject decision = new JSONObject();
        decision.put("action", "echo");
        decision.put("parameters", new JSONObject().put("directions", direction)); // direction should be enum of N,S,E or W change this
        return decision.toString();
    }
}