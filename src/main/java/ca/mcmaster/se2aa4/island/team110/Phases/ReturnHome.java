package ca.mcmaster.se2aa4.island.team110.Phases;

import ca.mcmaster.se2aa4.island.team110.Interfaces.Phase;
import org.json.JSONObject;

import ca.mcmaster.se2aa4.island.team110.Aerial.DroneController;
import ca.mcmaster.se2aa4.island.team110.Aerial.DroneHeading;
import ca.mcmaster.se2aa4.island.team110.RelativeMap;
import ca.mcmaster.se2aa4.island.team110.Records.Point;
import ca.mcmaster.se2aa4.island.team110.Records.Battery;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ReturnHome implements Phase {

   
    private DroneController droneController = new DroneController();
    private boolean isHome = false;
   
    
    private RelativeMap map;
    private Battery battery;

    private final Logger logger = LogManager.getLogger();

    public ReturnHome(RelativeMap map, Battery battery) {
        this.map = map;
        this.battery = battery;
    }


    @Override
    public boolean reachedEnd() {
        return isHome;
    }


    @Override
    public String getNextDecision() {
        logger.info("Phase: ReturnHome");
        Point current_position = map.getCurrentPosition();
        DroneHeading current_heading = map.getCurrentHeading();
        String heading_to_turn = "";

        if (current_position.x() == 0 && current_position.y() == 0){
            this.isHome = true;
            return droneController.stop();
        }

        if (current_position.x() > 1) {
            if (current_heading == DroneHeading.WEST) {
                map.updatePos();
                
                return droneController.fly();
            }
            else {
                heading_to_turn = determineTurnDirection(DroneHeading.WEST);
                return droneController.turn(heading_to_turn);
            }
        }

        else if (current_position.x() < -1) {
            if (current_heading == DroneHeading.EAST) {
                map.updatePos();
                return droneController.fly();
            }
            else {
                heading_to_turn = determineTurnDirection(DroneHeading.EAST);
                return droneController.turn(heading_to_turn);
            }
        }

        else if (current_position.y() > 0) {
            if (current_heading == DroneHeading.SOUTH) {
                map.updatePos();
                return droneController.fly();
            }
            else {
                heading_to_turn = determineTurnDirection(DroneHeading.SOUTH);
                return droneController.turn(heading_to_turn);
            }
        }

        else if (current_position.y() < 0) {
            if (current_heading == DroneHeading.NORTH) {
                map.updatePos();
                return droneController.fly();
            }
            else {
                heading_to_turn = determineTurnDirection(DroneHeading.NORTH);
                return droneController.turn(heading_to_turn);
            }
        }

        return null;
    }

    private String determineTurnDirection(DroneHeading target_heading) {
        DroneHeading current_heading = map.getCurrentHeading();
        String heading_to_turn = "";

        if (current_heading == DroneHeading.EAST) {
            
            switch(target_heading) {
                case NORTH:
                    map.updatePosTurn("LEFT");
                    heading_to_turn = "N";
                    break;
                case SOUTH:
                    map.updatePosTurn("RIGHT");
                    heading_to_turn = "S";
                    break;
                case EAST:
                    heading_to_turn = null;
                    break;
                case WEST:
                    map.updatePosTurn("RIGHT");
                    heading_to_turn = "S";
                    break;
            }
        }

        else if (current_heading == DroneHeading.NORTH) {
            
            switch(target_heading) {
                case NORTH:
                    heading_to_turn = null;
                    break;
                case SOUTH:
                    map.updatePosTurn("RIGHT");
                    heading_to_turn = "E";
                    break;
                case EAST:
                    map.updatePosTurn("RIGHT");
                    heading_to_turn = "E";
                    break;
                case WEST:
                    map.updatePosTurn("LEFT");
                    heading_to_turn = "W";
                    break;
            }
        }

        else if (current_heading == DroneHeading.WEST) {
            switch(target_heading) {
                case NORTH:
                    map.updatePosTurn("RIGHT");
                    heading_to_turn = "N";
                    break;
                case SOUTH:
                    map.updatePosTurn("LEFT");
                    heading_to_turn = "S";
                    break;
                case EAST:
                    map.updatePosTurn("RIGHT");
                    heading_to_turn = "N";
                    break;
                case WEST:
                    heading_to_turn = null;
                    break;
            }
        }

        else if (current_heading == DroneHeading.SOUTH) {
            switch(target_heading) {
                case NORTH:
                    map.updatePosTurn("RIGHT");
                    heading_to_turn = "W";
                    break;
                case SOUTH:
                    heading_to_turn = null;
                    break;
                case EAST:
                    map.updatePosTurn("LEFT");
                    heading_to_turn = "E";
                    break;
                case WEST:
                    map.updatePosTurn("RIGHT");
                    heading_to_turn = "W";
                    break;
            }
        }

        return heading_to_turn;
    }

    @Override
    public Phase getNextPhase() {
        return null;
    }

    public void updateState(JSONObject response) {
        Point current_position = map.getCurrentPosition();
        if (current_position.x() == 0 && current_position.y() == 0){
            this.isHome = true;
        }
        
    }

    public boolean isFinal() {
        return true;
    }

}
