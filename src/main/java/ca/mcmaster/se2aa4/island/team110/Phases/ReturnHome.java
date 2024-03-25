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
    private boolean done = false;
    private boolean hasUTurned;
    private boolean needsUTurn;
   
    private String turnDir = "";
   
    
    private RelativeMap map;
    private Battery battery;

    private State current_state;
    private int turnStage = 0;

    private final Logger logger = LogManager.getLogger();

    private enum State {
        FLY, U_TURN;
    }

    public ReturnHome(RelativeMap map, Battery battery) {
        this.map = map;
        this.battery = battery;

        this.current_state = State.FLY;
    }


    @Override
    public boolean reachedEnd() {
        return this.done;
    }


    @Override
    public String getNextDecision() {
        logger.info("Phase: ReturnHome");
        Point current_position = map.getCurrentPosition();
        DroneHeading current_heading = map.getCurrentHeading();
        String heading_to_turn = "";

        int x = current_position.x();
        int y = current_position.y();
        logger.info("x {}", x);
        logger.info("y {}", y);
        logger.info("current direction {}", current_heading);

        if (this.current_state == State.U_TURN) {
            return makeUTurn();
        }

        if (isHome){
            this.done = true;

            return droneController.stop();
        }

        //Corner case
        if (current_position.x() == 1 && current_position.y() == -1 && current_heading == DroneHeading.NORTH) {
            map.updatePosTurn("LEFT");
            return droneController.turn("W");
        }
        
        else if (current_position.x() == 1 && current_position.y() == 1 && current_heading == DroneHeading.SOUTH) {
            map.updatePosTurn("RIGHT");
            return droneController.turn("W");
        }

        else if (current_position.x() == -1 && current_position.y() == -1 && current_heading == DroneHeading.NORTH) {
            map.updatePosTurn("RIGHT");
            return droneController.turn("E");
        }

        else if (current_position.x() == -1 && current_position.y() == 1 && current_heading == DroneHeading.SOUTH) {
            map.updatePosTurn("LEFT");
            return droneController.turn("E");
        }


        //Edge case where facing awkward direction 
        else if (current_position.x() == 1 && current_heading == DroneHeading.SOUTH) {
            logger.info("1");
            this.needsUTurn = true;
            config(2);
            return makeUTurn();
        }

        else if (current_position.x() == 1 && current_heading == DroneHeading.NORTH) {
            logger.info("2");
            this.needsUTurn = true;
            config(1);
            return makeUTurn();
        }

        else if (current_position.x() == -1 && current_heading == DroneHeading.SOUTH) {
            logger.info("3");
            this.needsUTurn = true;
            config(1);
            return makeUTurn();
        }

        else if (current_position.x() == -1 && current_heading == DroneHeading.NORTH) {
            logger.info("4");
            this.needsUTurn = true;
            config(2);
            return makeUTurn();
        }


        // regular cases
        else if (current_position.x() > 1) {
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


    private String makeUTurn() {
        DroneHeading current_heading = map.getCurrentHeading();
        switch (turnStage) {
            case 0:
                logger.info("direction {}", current_heading);
                turnStage++;
           
                return droneController.turn(getUTurnHeadingDir());
            case 1:
                logger.info("direction {}", current_heading);
                turnStage++;
                return droneController.turn(getUTurnHeadingDir());
            case 2:
                logger.info("direction {}", current_heading);
                this.hasUTurned = true;
                this.needsUTurn = false;
                turnStage = 0;
                return droneController.turn(getUTurnHeadingDir());
            default:
                return null;
        }
    }

    private void config(int version) {
        if (version == 1) {
            this.turnDir = "RIGHT";
        }
        else if (version == 2) {
            this.turnDir = "LEFT";
        }
    }

    private String getUTurnHeadingDir() {
        DroneHeading current_heading = map.getCurrentHeading();
        String headingDir = "";
        if (current_heading == DroneHeading.NORTH && this.turnDir.equals("RIGHT")) {
            logger.info("3");
            map.updatePosTurn("RIGHT");
            headingDir = "E";
        }
        else if (current_heading == DroneHeading.NORTH && this.turnDir.equals("LEFT")) {
            logger.info("im here");
            map.updatePosTurn("LEFT");
            headingDir = "W";
        }
        else if (current_heading == DroneHeading.EAST && this.turnDir.equals("RIGHT")) {
            logger.info("4");
            map.updatePosTurn("RIGHT");
            headingDir = "S";
        }
        else if (current_heading == DroneHeading.EAST && this.turnDir.equals("LEFT")) {
            logger.info("5");
            map.updatePosTurn("LEFT");
            headingDir = "N";
        }
        else if (current_heading == DroneHeading.SOUTH && this.turnDir.equals("RIGHT")) {
            logger.info("lol");
            map.updatePosTurn("RIGHT");
            headingDir = "W";
        }
        else if (current_heading == DroneHeading.SOUTH && this.turnDir.equals("LEFT")) {
            logger.info("6");
            map.updatePosTurn("LEFT");
            headingDir = "E";
        }

        else if (current_heading == DroneHeading.WEST && this.turnDir.equals("RIGHT")) {
            logger.info("7");
            map.updatePosTurn("RIGHT");
            headingDir = "N";
        }
        else if (current_heading == DroneHeading.WEST && this.turnDir.equals("LEFT")) {
            map.updatePosTurn("LEFT");
            headingDir = "S";
        }
        return headingDir;
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
        else if (this.needsUTurn) {
            this.current_state = State.U_TURN;
        }
        else {
            this.current_state = State.FLY;
        }

        
    }

    public boolean isFinal() {
        return true;
    }

}
