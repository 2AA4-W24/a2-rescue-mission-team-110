package ca.mcmaster.se2aa4.island.team110.Phases;

import org.json.JSONArray;
import org.json.JSONObject;

import ca.mcmaster.se2aa4.island.team110.RelativeMap;
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

  private RelativeMap map;

  private State current = State.SCAN;
  private int turnStage = 0;
  private String turnDirr;
  private Direction currDir;

  private boolean isOutOfRange = false;
  private boolean hasUturned = false;
  private boolean waitingForEcho = false;
  private int groundDis = -2;
  private String incomingDirection;

  public void setDirection(String direction) {
    this.incomingDirection = direction; // N/S or E/W
    this.currDir = Direction.valueOf(incomingDirection);
    determineTurnDirection();
  }

  private void determineTurnDirection() {
    if ("N".equals(incomingDirection) || "S".equals(incomingDirection)) {
        this.turnDirr = "E";
    } else if ("E".equals(incomingDirection) || "W".equals(incomingDirection)) {
        this.turnDirr = "S";
    }
    
  }

  


  public iFirstPass(RelativeMap map) {
    this.map = map;
  }

  private enum State {
    ECHO, FLY, SCAN, U_TURN, ECHO2, FLY2
  }

  private enum Direction {
    N, S, E, W
  }

  @Override
  public boolean reachedEnd() {
    return isOutOfRange;
  }

  private String makeUTurn() {
    switch (turnStage) {
      case 0:
        turnStage++;
        return droneController.turn(turnDirr);
      case 1:
        turnStage++;
        String directionToTurn = (currDir == Direction.S) ? "N" : "S";
        currDir = (currDir == Direction.S) ? Direction.N : Direction.S;
        logger.info("Turn: {}", directionToTurn);
        return droneController.turn(directionToTurn);
      case 2:
        current = State.SCAN;
        turnStage = 0;
        hasUturned = true;
        return droneRadar.echo(currDir == Direction.N ? "N" : "S");
      default:
        return null;
    }
  }

  @Override
  public String getNextDecision() {
    logger.info("Phase: iFirstPass");

    if (current == State.FLY2 && groundDis >= 0) {
      groundDis--;
      logger.error("Flying towards ground, distance left: {}", groundDis);
    }

    switch (current) {
      case ECHO:
        current = State.SCAN;
        return droneRadar.echo(currDir == Direction.N ? "N" : "S");
      case SCAN:
        current = State.FLY;
        hasUturned = false;
        return droneScanner.scan();
      case FLY:
        current = State.ECHO;
        return droneController.fly();
      case U_TURN:
        return makeUTurn();
      case ECHO2:
        return droneRadar.echo(currDir == Direction.N ? "N" : "S");
      case FLY2:
        if (groundDis == -1) {
          current = State.SCAN;
          groundDis = -2;
        }
        return droneController.fly();
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
          if (hasUturned) {
            logger.info("hasUturned is True");
            isOutOfRange = true;
          } else {
            current = State.U_TURN;
          }
        }
      }
      if (extras.has("biomes")) {
        JSONArray biomes = extras.getJSONArray("biomes");
        if (biomes.length() == 1 && "OCEAN".equals(biomes.getString(0)) && !hasUturned) {
          waitingForEcho = true;
          current = State.ECHO2;
        }
      }
      if (waitingForEcho && extras.has("range")) {
        groundDis = extras.getInt("range");
        logger.info("Ground distance updated to: {}", groundDis);
        waitingForEcho = false;
        current = State.FLY2;
      }
    }
  }
}