package ca.mcmaster.se2aa4.island.team110.Phases;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import ca.mcmaster.se2aa4.island.team110.Aerial.DroneController;
import ca.mcmaster.se2aa4.island.team110.Aerial.DroneRadar;
import ca.mcmaster.se2aa4.island.team110.Interfaces.Phase;
import ca.mcmaster.se2aa4.island.team110.RelativeMap;

public class FindGround implements Phase {
  private final Logger logger = LogManager.getLogger();

  private enum State {
    FIND_GROUND, GO_TO_GROUND, FLY
  }

  private DroneController droneController = new DroneController();
  private DroneRadar droneRadar = new DroneRadar();
  private State current;
  private RelativeMap map;

  private boolean groundDetected = false;
  private boolean turnCompleted = false;


  public FindGround(RelativeMap map) {
    this.map = map;
    this.current = State.FIND_GROUND;
  }

  public void setToFly() {
    this.current = State.FLY;
  }

  @Override
  public boolean reachedEnd() {
    return turnCompleted;
  }

  @Override
  public String getNextDecision() {
    logger.info("Phase: FindGround");
    switch (current) {
      case FIND_GROUND:
        // Echo south, change state
        current = State.FLY;
        return droneRadar.echo("S");
      case GO_TO_GROUND:
        // Turn south, mark turn completed
        current = State.FLY;
        turnCompleted = true;
        return droneController.turn("S");
      case FLY:
        // Fly, then go back to finding ground state
        current = State.FIND_GROUND;
        return droneController.fly();
      default:
        return droneController.fly();
    }
  }

  public void groundResponse(boolean groundFound) {
    groundDetected = groundFound;
    if (groundDetected) {
      current = State.GO_TO_GROUND;
    } else {
      current = State.FLY;
    }
  }

  @Override
  public Phase getNextPhase() {
    return new MoveToGround();
  }

  @Override
  public void updateState(JSONObject response) {
    if (response.has("extras")) {
      JSONObject extras = response.getJSONObject("extras");
      if (extras.has("found")) {
        if ("GROUND".equals(extras.getString("found"))) {
          groundResponse(true);
        } else {
          setToFly();
        }
      }
    }
  }
}
