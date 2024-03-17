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

public class MoveToGround implements Phase {
  private final Logger logger = LogManager.getLogger();

  private DroneController droneController = new DroneController();
  private DroneScanner droneScanner = new DroneScanner();
  private DroneRadar droneRadar = new DroneRadar();

  private RelativeMap map;

  private State current;
  private int range = -1;

  private boolean hasScanGround = false;

  public MoveToGround(RelativeMap map) {
    this.map = map;
  }

  private enum State {
    FLY, SCAN, ECHO
  }

  public void setHasScanGround(boolean hasScanGround) {
    this.hasScanGround = hasScanGround;
  }

  public MoveToGround() {
    this.current = State.ECHO;
  }

  @Override
  public boolean reachedEnd() {
    return hasScanGround;
  }

  @Override
  public String getNextDecision() {
    logger.info("Phase: MoveToGround");
    if (range == -1) {
      current = State.ECHO;
    } else if (range > 0) {
      current = State.FLY;
    } else if (range == 0) {
      current = State.SCAN;
    }

    switch (current) {
      case ECHO:
        return droneRadar.echo("S");
      case SCAN:
        current = State.FLY;
        return droneScanner.scan();
      case FLY:
        if (range > 0) {
          range--;
        }
        if (range == 0) {
          hasScanGround = true;
        }
        return droneController.fly();
      default:
        return droneController.fly();
    }
  }

  @Override
  public Phase getNextPhase() {
    return new iFirstPass(map);
  }

  @Override
  public void updateState(JSONObject response) {
    if (response.has("extras")) {
      JSONObject extras = response.getJSONObject("extras");
      if (extras.has("range")) {
        range = extras.getInt("range");
        logger.info("Range updated to: {}", range);
      }
    }
  }
}