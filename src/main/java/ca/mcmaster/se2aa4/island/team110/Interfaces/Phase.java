package ca.mcmaster.se2aa4.island.team110.Interfaces;

import org.json.JSONObject;

public interface Phase {
    boolean reachedEnd();
    String getNextDecision();
    Phase getNextPhase();
    void updateState(JSONObject response);
    boolean isFinal();
}
