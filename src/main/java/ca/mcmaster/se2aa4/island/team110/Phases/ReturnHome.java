package ca.mcmaster.se2aa4.island.team110.Phases;

import ca.mcmaster.se2aa4.island.team110.Interfaces.Phase;
import org.json.JSONObject;

public class ReturnHome implements Phase {

  @Override
  public boolean reachedEnd() {
    return false;
  }

  @Override
    public String getNextDecision() {
        return "hello";
    }

  @Override
  public Phase getNextPhase() {
      return null;
  }

  @Override
  public void updateState(JSONObject response) {
    if (response.has("extras")) {
      JSONObject extras = response.getJSONObject("extras");
      if (extras.has("range")) {
        
      }
    }
  }
  
}
