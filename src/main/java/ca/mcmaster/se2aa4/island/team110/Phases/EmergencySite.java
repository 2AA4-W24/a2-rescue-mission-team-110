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

public class EmergencySite implements Phase{
  private DroneController droneController = new DroneController();
  private boolean reachedEmergencySite = false;
  private boolean hasReachedLand = false;
  private int crew = 5;
  
  
  
  private RelativeMap map;

  private final Logger logger = LogManager.getLogger();

  public EmergencySite(RelativeMap map) {
      this.map = map;
      
  }

  



  @Override
  public boolean reachedEnd() {
      return reachedEmergencySite;
  }


  @Override
  public String getNextDecision() {
      logger.info("Phase: ReturnHome");
      Point current_position = map.getCurrentPosition();
      DroneHeading current_heading = map.getCurrentHeading();
      
      Point emergencySiteLocation = map.getEmergencySiteLocation();
      logger.info("x:" + current_position.x() + " y:" + current_position.y());

      if (!hasReachedLand){
        hasReachedLand = true;
        String closestCreekId = map.getClosestCreekId();
        Point closestCreekPosition = map.getClosestCreekPosition(); 
        map.setCurrentPosition(closestCreekPosition.x(), closestCreekPosition.y());
        return droneController.land(closestCreekId, crew-1);
      }

      

      if (current_position.x() == emergencySiteLocation.x() && current_position.y() == emergencySiteLocation.y()){
          this.reachedEmergencySite = true;
          return droneController.stop();
      }

      if (current_position.x() > emergencySiteLocation.x() ) {
        if (current_heading == DroneHeading.WEST){
          map.updatePos();
          return droneController.move_to("W");
        }
        else{
          current_heading = DroneHeading.WEST;
          map.updatePos();
          return droneController.move_to("W");
        }
        
      }
      else if (current_position.x() < emergencySiteLocation.x()){
        if (current_heading == DroneHeading.EAST){
          map.updatePos();
          return droneController.move_to("E");
        }
        else{
          current_heading = DroneHeading.EAST;
          map.updatePos();
          return droneController.move_to("E");
        }
      }
      else if (current_position.y() > emergencySiteLocation.y()){
        if (current_heading == DroneHeading.SOUTH){
          map.updatePos();
          return droneController.move_to("S");
        }
        else{
          current_heading = DroneHeading.SOUTH;
          map.updatePos();
          return droneController.move_to("S");
        }
      } 
      else if (current_position.y() < emergencySiteLocation.y()){
        if (current_heading == DroneHeading.NORTH){
          map.updatePos();
          return droneController.move_to("N");
        }
        else{
          current_heading = DroneHeading.NORTH;
          map.updatePos();
          return droneController.move_to("N");
        }
      }

      return null;
  }

  
  @Override
  public Phase getNextPhase() {
      return null;
  }

  public void updateState(JSONObject response) {
      Point current_position = map.getCurrentPosition();
      Point emergencySiteLocation = map.getEmergencySiteLocation();
      if (current_position.x() == emergencySiteLocation.x() && current_position.y() == emergencySiteLocation.y()){
          this.reachedEmergencySite = true;
      }
      
  }

  public boolean isFinal() {
      return true;
  }
  
}
