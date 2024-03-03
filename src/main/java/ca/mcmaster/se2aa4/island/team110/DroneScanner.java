package ca.mcmaster.se2aa4.island.team110;

import org.json.JSONObject;
import ca.mcmaster.se2aa4.island.team110.interfaces.Scanner;

public class DroneScanner implements Scanner {
    @Override
    public String scan() {
        JSONObject decision = new JSONObject();
        decision.put("action", "scan");
        return decision.toString(); 
    }

}
