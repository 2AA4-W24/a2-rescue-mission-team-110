package ca.mcmaster.se2aa4.island.team110.Phases;

import org.json.JSONArray;
import org.json.JSONObject;

import ca.mcmaster.se2aa4.island.team110.RelativeMap;
import ca.mcmaster.se2aa4.island.team110.Aerial.DroneController;
import ca.mcmaster.se2aa4.island.team110.Aerial.DroneRadar;
import ca.mcmaster.se2aa4.island.team110.Aerial.DroneScanner;
import ca.mcmaster.se2aa4.island.team110.Aerial.DroneHeading;

import ca.mcmaster.se2aa4.island.team110.Interfaces.Phase;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class iFirstPass implements Phase {

  private final Logger logger = LogManager.getLogger();
  private DroneController droneController = new DroneController();
  private DroneRadar droneRadar = new DroneRadar();
  private DroneScanner droneScanner = new DroneScanner();
  private DroneHeading currDir;
  private DroneHeading previousDir;

  private RelativeMap map;

  private State current = State.SCAN;
  private int turnStage = 0;
 

  private boolean isOutOfRange = false;
  private boolean hasUturned = false;
  private boolean waitingForEcho = false;
  private int groundDis = -2;
  private String directionToTurn = "";
  private String mapDirUpdate = "";
  
  private String echohere;

  public iFirstPass(RelativeMap map, DroneHeading direction) {
    this.map = map;
    this.currDir = direction;
    
  }

  private enum State {
    ECHO, FLY, SCAN, U_TURN, ECHO2, FLY2
  }

  // private enum Direction {
  //   N, S, E
  // }

  @Override
  public boolean reachedEnd() {
    return isOutOfRange;
  }

  public void determineEcho() {
    if (currDir == DroneHeading.NORTH || currDir == DroneHeading.SOUTH){
      this.echohere = currDir == DroneHeading.NORTH ? "N" : "S";
    } 
    else if (currDir == DroneHeading.EAST || currDir == DroneHeading.WEST){
      this.echohere = currDir == DroneHeading.EAST ? "E" : "W";
    }
  }

  private String makeUTurn() {
    switch (turnStage) {
      case 0:
        turnStage++;
        if (this.currDir == DroneHeading.SOUTH) {
          this.directionToTurn = "E";
          this.mapDirUpdate = "LEFT";
        }
        else if (this.currDir == DroneHeading.NORTH) {
          this.directionToTurn = "E";
          this.mapDirUpdate = "RIGHT";
        }
       
        this.previousDir = this.currDir;
        this.currDir = this.currDir.turn(this.mapDirUpdate);
        return droneController.turn(directionToTurn);

      case 1:
        turnStage++;
        if (this.previousDir == DroneHeading.SOUTH) {
          
          this.directionToTurn = "N";
          this.mapDirUpdate = "LEFT";
         
        }
        else if (this.previousDir == DroneHeading.NORTH) {
          this.directionToTurn = "S";
          this.mapDirUpdate = "RIGHT";
        }
        else if (this.previousDir == DroneHeading.EAST) {
          this.directionToTurn = "W";
          this.mapDirUpdate = "LEFT";
        }
        else if (this.previousDir == DroneHeading.WEST) {
          this.directionToTurn = "E";
          this.mapDirUpdate = "RIGHT";
        }

        this.currDir = this.currDir.turn(this.mapDirUpdate);
        logger.info("Turn: {}", directionToTurn);

        return droneController.turn(directionToTurn);
      case 2:
        current = State.SCAN;
        turnStage = 0;
        hasUturned = true;
        determineEcho();
        return droneRadar.echo(this.echohere);
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
        determineEcho();
        return droneRadar.echo(this.echohere);
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
        determineEcho();
        return droneRadar.echo(this.echohere);
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
    return new iSecondPass(this.map, this.currDir);
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
        if (biomes.length() == 1 && "OCEAN".equals(biomes.getString(0))) {
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