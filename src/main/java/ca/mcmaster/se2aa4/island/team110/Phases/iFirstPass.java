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
  public boolean checkEchoAfterTurn = false;

  private enum State {
    ECHO, FLY, SCAN, U_TURN
  }

  private enum Direction {
    N, S
  }

  @Override
  public boolean reachedEnd() {
    return isOutOfRange;
  }

  public void canUTurn(String response) {
    if ("OUT_OF_RANGE".equals(response)) {
      current = State.U_TURN;
    } else {
      current = State.FLY;
    }
  }

  private String makeUTurn() {
    switch (turnStage) {
      case 0:
        turnStage++;
        return droneController.fly();
      case 1:
        turnStage++;
        return droneController.turn("E");
      case 2:
        turnStage++;
        return droneController.fly();
      case 3:
        turnStage++;
        if (currDir == Direction.S) {
          currDir = Direction.N;
          return droneController.turn("N");
        } else if (currDir == Direction.N) {
          currDir = Direction.S;
          current = State.ECHO;
          return droneController.turn("S");
        }
      case 4:
        turnStage = 0;
        checkEchoAfterTurn = true;
        current = State.FLY;
        return droneRadar.echo(currDir == Direction.N ? "N" : "S");
      default:
        return null;
    }
  }

  @Override
  public String getNextDecision() {
    if (checkEchoAfterTurn) {
      checkEchoAfterTurn = false;
      current = State.ECHO;
      return droneRadar.echo(currDir == Direction.N ? "N" : "S");
    } else {
      switch (current) {
        case ECHO:
          current = State.FLY;
          if (currDir == Direction.N) {
            return droneRadar.echo("N");
          } else {
            return droneRadar.echo("S");
          }
        case FLY:
          current = State.SCAN;
          return droneController.fly();
        case SCAN:
          current = State.ECHO;
          return droneScanner.scan();
        case U_TURN:
          return makeUTurn();
        default:
          return null;
      }
    }

  }

  public void processEchoResultAfterUTurn(String response) {
    if ("OUT_OF_RANGE".equals(response)) {
      isOutOfRange = true;
      checkEchoAfterTurn = false;
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
        canUTurn(extras.getString("found"));
        if ("OUT_OF_RANGE".equals(extras.getString("found")) && checkEchoAfterTurn) {
          processEchoResultAfterUTurn(extras.getString("found"));
        }
      }
    }
  }
}
