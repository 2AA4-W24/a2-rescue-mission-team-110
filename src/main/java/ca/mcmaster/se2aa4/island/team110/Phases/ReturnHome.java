package ca.mcmaster.se2aa4.island.team110.Phases;

import ca.mcmaster.se2aa4.island.team110.Interfaces.Phase;
import org.json.JSONObject;

import ca.mcmaster.se2aa4.island.team110.Aerial.DroneController;
import ca.mcmaster.se2aa4.island.team110.Aerial.DroneHeading;
import ca.mcmaster.se2aa4.island.team110.RelativeMap;
import ca.mcmaster.se2aa4.island.team110.Records.Point;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ReturnHome implements Phase {

  private RelativeMap map;
  private DroneController droneController = new DroneController();
  private boolean isHome = false;
  private final Logger logger = LogManager.getLogger();

  public ReturnHome(RelativeMap map) {
    this.map = map;
  }

  @Override
  public boolean reachedEnd() {
    return isHome;
  }



  @Override
  public String getNextDecision() {
    logger.info("Phase: ReturnHome");
    Point currentPos = map.getCurrentPosition();

    if (currentPos.x() == 0 && currentPos.y() == 0){
      isHome = false;
      return droneController.stop();
    }

    return goHome(currentPos);
  }

  private String goHome(Point currentPos){
    int x = currentPos.x();
    int y = currentPos.y();

    DroneHeading heading = map.getCurrentHeading();

    if (x > 1 ) {
      if (heading != DroneHeading.WEST)
        return droneRotate(DroneHeading.WEST);
    } else if (x < 1){
      if (heading != DroneHeading.EAST)
        return droneRotate(DroneHeading.EAST);
    } else if (y<0){
      if (heading!= DroneHeading.NORTH)
        return droneRotate(DroneHeading.NORTH);
    } else if (y>0){
      if (heading != DroneHeading.SOUTH)
        return droneRotate(DroneHeading.SOUTH);
    }

    map.updatePos();
    return droneController.fly();
  }

  private String droneRotate(DroneHeading homeHeading){
    DroneHeading currentHeading = map.getCurrentHeading();

    while (currentHeading != homeHeading){
      map.updatePosTurn("RIGHT");
      currentHeading= map.getCurrentHeading();

      switch (currentHeading){
        case NORTH:
          droneController.turn("E");
          currentHeading = DroneHeading.EAST;
          break;
        case EAST:
          droneController.turn("S");
          currentHeading = DroneHeading.SOUTH;
          break;
        case SOUTH:
          droneController.turn("W");
          currentHeading = DroneHeading.WEST;
          break;
        case WEST:
          droneController.turn("N");
          currentHeading = DroneHeading.NORTH;
          break;
      }
    }

    
    return "Heading home";
    
  }



  @Override
  public Phase getNextPhase() {
      return null;
  }

  @Override
  public void updateState(JSONObject response) {
  }
  
}
