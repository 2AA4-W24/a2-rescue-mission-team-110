package ca.mcmaster.se2aa4.island.team110.Phases;

import org.json.JSONObject;

import ca.mcmaster.se2aa4.island.team110.Aerial.DroneController;
import ca.mcmaster.se2aa4.island.team110.Aerial.DroneRadar;
import ca.mcmaster.se2aa4.island.team110.Aerial.DroneScanner;
import ca.mcmaster.se2aa4.island.team110.Interfaces.Phase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class iFirstPass implements Phase {

  private final Logger logger = LogManager.getLogger();
  private DroneController droneController = new DroneController();
  private DroneRadar droneRadar = new DroneRadar();
  private DroneScanner droneScanner = new DroneScanner();

  private State current = State.ECHO;
  private int turnStage = 0;
  private Direction currDir = Direction.S;

  private boolean isOutOfRange = false;

  private enum State {
    ECHO, FLY, SCAN, U_TURN
  }

  private enum Direction {
    N, S, E
  }

  @Override
  public boolean reachedEnd() {
    return isOutOfRange;
  }

  private String makeUTurn() {
    switch (turnStage) {
      case 0:
        turnStage++;
        logger.info("turn: East");
        current = State.U_TURN;
        return droneController.turn("E");
      case 1:
        turnStage++;
        String directionToTurn = (currDir == Direction.S) ? "N" : "S";
        currDir = (currDir == Direction.S) ? Direction.N : Direction.S;
        current = State.U_TURN;
        logger.info("Turn: {}", directionToTurn);
        return droneController.turn(directionToTurn);
      case 2:
        current = State.SCAN;
        turnStage = 0;
        return droneRadar.echo(currDir == Direction.N ? "N" : "S");
      default:
        return null;
    }
  }

  @Override
  public String getNextDecision() {
    logger.info("Phase: iFirstPass");
    switch (current) {
      case ECHO:
        current = State.SCAN;
        return droneRadar.echo(currDir == Direction.N ? "N" : "S");
      case SCAN:
        current = State.FLY;
        return droneScanner.scan();
      case FLY:
        current = State.ECHO;
        return droneController.fly();
      case U_TURN:
        return makeUTurn();
      default:
        return null;
    }
  }

  @Override
  public Phase getNextPhase() {
    return null;
  }

  @Override
  public void updateState(JSONObject response) {
    if (response.has("extras")) {
      JSONObject extras = response.getJSONObject("extras");
      if (extras.has("found")) {
        if ("OUT_OF_RANGE".equals(extras.getString("found"))) {
          if(extras.has("range")){
            int range = extras.getInt("range");
            if(range < 10){
              current = State.U_TURN;
            }
            isOutOfRange = true;
          }
        }
      }
    }
  }
}