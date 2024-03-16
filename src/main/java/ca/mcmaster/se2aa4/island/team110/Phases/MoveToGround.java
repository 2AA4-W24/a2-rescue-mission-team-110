package ca.mcmaster.se2aa4.island.team110.Phases;

import org.json.JSONArray;
import org.json.JSONObject;

import ca.mcmaster.se2aa4.island.team110.Aerial.DroneController;
import ca.mcmaster.se2aa4.island.team110.Aerial.DroneScanner;
import ca.mcmaster.se2aa4.island.team110.Interfaces.Phase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MoveToGround implements Phase {
  private final Logger logger = LogManager.getLogger();


  private DroneController droneController = new DroneController();
  private DroneScanner droneScanner = new DroneScanner();
  private State currentState;

  private boolean hasScanGround = false;

  private enum State {
    FLY, SCAN
  }

  public void setHasScanGround(boolean hasScanGround) {
    this.hasScanGround = hasScanGround;
  }

  public MoveToGround() {
    this.currentState = State.SCAN;
  }

  @Override
  public boolean reachedEnd() {
    return hasScanGround;
  }

  @Override
  public String getNextDecision() {
    if (!hasScanGround) {
      if (currentState == State.SCAN) {
        currentState = State.FLY;
        return droneScanner.scan();
      } else {
        currentState = State.SCAN;
        return droneController.fly();
      }

    }
    return null;

  }

  @Override
  public Phase getNextPhase() {
    return new iFirstPass();
  }

  @Override
  public boolean isFinal() {
    return false;
  }

  @Override
  public void updateState(JSONObject response) {
    if (response.has("extras")) {
      JSONObject extras = response.getJSONObject("extras");
      if (extras.has("biomes")) {
        JSONArray biomes = extras.getJSONArray("biomes");
        if (!(biomes.length()== 1 && "OCEAN".equals(biomes.getString(0)))){
          setHasScanGround(true);
        }
      }
    }
  }
}
