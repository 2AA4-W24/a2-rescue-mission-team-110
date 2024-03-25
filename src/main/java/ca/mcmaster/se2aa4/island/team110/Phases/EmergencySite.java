package ca.mcmaster.se2aa4.island.team110.Phases;

import ca.mcmaster.se2aa4.island.team110.Interfaces.Phase;
import org.json.JSONObject;

import ca.mcmaster.se2aa4.island.team110.Aerial.DroneController;
import ca.mcmaster.se2aa4.island.team110.Aerial.DroneHeading;
import ca.mcmaster.se2aa4.island.team110.RelativeMap;
import ca.mcmaster.se2aa4.island.team110.Records.Point;
import eu.ace_design.island.game.Crew;
import scala.Int;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EmergencySite implements Phase {
  private DroneController droneController = new DroneController();
  private DroneHeading previous_heading;
  private boolean reachedEmergencySite = false;
  private int crew = 5;
  private State current_state;

  private RelativeMap map;

  private final Logger logger = LogManager.getLogger();

  public EmergencySite(RelativeMap map) {
    this.map = map;
    this.current_state = State.LAND;
    this.previous_heading = map.getCurrentHeading();
  }

  private enum State {
    LAND, STOP, MOVE
  }

  @Override
  public boolean reachedEnd() {
    return reachedEmergencySite;
  }

  @Override
  public String getNextDecision() {
    logger.info("Phase: ReturnHome");
    Point current_position = map.getCurrentPosition();

    Point emergencySiteLocation = map.getEmergencySiteLocation();
    logger.info("Current Pos - x:" + current_position.x() + " y:" + current_position.y());
    logger.info("EmergencySite Pos - x:" + emergencySiteLocation.x() + " y:" + emergencySiteLocation.y());

    switch (current_state) {
      case LAND:
        String closestCreekId = map.getClosestCreekId();
        Point closestCreekPosition = map.getClosestCreekPosition();
        map.setCurrentPosition(closestCreekPosition.x(), closestCreekPosition.y());
        current_state = State.MOVE;
        return droneController.land(closestCreekId, crew - 1);
      case STOP:
        this.reachedEmergencySite = true;
        return droneController.stop();
      case MOVE:
        if (current_position.x() > emergencySiteLocation.x()) {
          map.updatePosMoveTo(DroneHeading.WEST);
          logger.info("Moving W");
          return droneController.move_to("W");
        }
        else if (current_position.x() < emergencySiteLocation.x()) {
          map.updatePosMoveTo(DroneHeading.EAST);
          logger.info("Moving E");
          return droneController.move_to("E");
        }
        else if (current_position.y() > emergencySiteLocation.y()) {
          map.updatePosMoveTo(DroneHeading.SOUTH);
          logger.info("Moving S");
          return droneController.move_to("S");
        }
        else if (current_position.y() < emergencySiteLocation.y()) {
          map.updatePosMoveTo(DroneHeading.NORTH);
          logger.info("Moving N");
          return droneController.move_to("N");
        }
      default:
        return null;
    }
  }

  @Override
  public Phase getNextPhase() {
    return null;
  }

  public void updateState(JSONObject response) {
    Point current_position = map.getCurrentPosition();
    Point emergencySiteLocation = map.getEmergencySiteLocation();
    if (current_position.x() == emergencySiteLocation.x() && current_position.y() == emergencySiteLocation.y()) {
      current_state = State.STOP;
    }
  }

  public boolean isFinal() {
    return true;
  }

}
