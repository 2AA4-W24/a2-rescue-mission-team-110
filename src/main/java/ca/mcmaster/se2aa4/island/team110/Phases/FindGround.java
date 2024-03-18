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
    FIND_GROUND_S, FIND_GROUND_N, FIND_GROUND_E, FIND_GROUND_W, GO_TO_GROUND, FLY
  }

  private DroneController droneController = new DroneController();
  private DroneRadar droneRadar = new DroneRadar();
  private State current;
  private RelativeMap map;

  private boolean groundDetected = false;
  private boolean turnCompleted = false;
  private String compass;


  public FindGround(RelativeMap map) {
    this.map = map;
    this.current = State.FIND_GROUND_S;
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
      case FIND_GROUND_S: // Echo south, change state
        compass = "S";
        current = State.FIND_GROUND_N;
        return droneRadar.echo(compass);

      case FIND_GROUND_N: // Echo North change state
        compass = "N";  
        current = State.FIND_GROUND_E;
        return droneRadar.echo(compass);

      case FIND_GROUND_E: // Echo east change state
        compass = "E";
        current = State.FIND_GROUND_W;
        return droneRadar.echo(compass);

      case FIND_GROUND_W: //Echo West, change state
        compass = "S";
        current = State.FLY;
        return droneRadar.echo(compass);

      case GO_TO_GROUND: // Turn to direction of ground, mark turn completed
        
        current = State.FLY;
        turnCompleted = true;
        return droneController.turn(compass);

      case FLY:     // Fly, then go back to finding ground state
        current = State.FIND_GROUND_S;
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
    MoveToGround moveToGround= new MoveToGround(map);
    moveToGround.setDirection(compass); 
    return moveToGround;
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
