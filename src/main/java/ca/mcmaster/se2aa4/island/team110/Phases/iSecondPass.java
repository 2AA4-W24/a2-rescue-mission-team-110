package ca.mcmaster.se2aa4.island.team110.Phases;

import org.json.JSONArray;
import org.json.JSONObject;

import ca.mcmaster.se2aa4.island.team110.Aerial.DroneController;
import ca.mcmaster.se2aa4.island.team110.Aerial.DroneRadar;
import ca.mcmaster.se2aa4.island.team110.Aerial.DroneScanner;
import ca.mcmaster.se2aa4.island.team110.Interfaces.Phase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class iSecondPass implements Phase {
    public boolean reachedEnd() {
        return false;
    }
    public String getNextDecision() {
        return "Lol";

    }
    public Phase getNextPhase() {
        return null;

    }
    public void updateState(JSONObject response) {

    }

}