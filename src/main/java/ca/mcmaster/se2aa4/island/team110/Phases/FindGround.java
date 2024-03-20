package ca.mcmaster.se2aa4.island.team110.Phases;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import ca.mcmaster.se2aa4.island.team110.Aerial.DroneController;
import ca.mcmaster.se2aa4.island.team110.Aerial.DroneRadar;
import ca.mcmaster.se2aa4.island.team110.Aerial.DroneHeading;

import ca.mcmaster.se2aa4.island.team110.Interfaces.Phase;
import ca.mcmaster.se2aa4.island.team110.Records.Point;
import ca.mcmaster.se2aa4.island.team110.RelativeMap;

public class FindGround implements Phase {
    private final Logger logger = LogManager.getLogger();

    private enum State {
        FIND_GROUND, GO_TO_GROUND, FLY
    }

    private DroneController droneController = new DroneController();
    private DroneRadar droneRadar = new DroneRadar();

    private State current_state;

    private RelativeMap map;

    private String lastEchoDirection = null;

    private boolean groundDetected = false;
    private boolean turnCompleted = false;


    public FindGround(RelativeMap map) {
        this.map = map;
        this.current_state = State.FIND_GROUND;
     }

    public void setToFly() {
        this.current_state = State.FLY;
    }

    private String getAndAlternateEchoDirection() {
        String[] echoDirections;
        if (DroneHeading.NORTH.equals(map.getCurrentHeading()) || DroneHeading.SOUTH.equals(map.getCurrentHeading())) {
            echoDirections = new String[]{"E", "W"};
        } 
        else { 
            echoDirections= new String[]{"N","S"};
        }

        
        if (lastEchoDirection == null || !lastEchoDirection.equals(echoDirections[0])) {
            lastEchoDirection = echoDirections[0];
        } 
        else {
            lastEchoDirection= echoDirections[1];
        }

    return lastEchoDirection;
}



    @Override
    public boolean reachedEnd() {
        return turnCompleted;
    }

    @Override
    public String getNextDecision() {
        logger.info("Phase: FindGround");
        switch (current_state) {
            case FIND_GROUND:
                String nextEchoDirection = getAndAlternateEchoDirection();
                current_state = State.FLY;
                return droneRadar.echo(nextEchoDirection);
            case GO_TO_GROUND:
                current_state = State.FLY;
                turnCompleted = true;
                map.updatePosTurn("RIGHT");// relative map
                DroneHeading direction = map.getCurrentHeading();
                logger.info("The direction of the drone is {}", direction);
                return droneController.turn(lastEchoDirection);
            case FLY:
                current_state = State.FIND_GROUND;
                map.updatePos();
                return droneController.fly();
            default:
                map.updatePos();
                return droneController.fly();
        }
    }

    public void groundResponse(boolean groundFound) {
        groundDetected = groundFound;
        if (groundDetected) {
            current_state = State.GO_TO_GROUND;
        } else {
            current_state = State.FLY;
        }
    }

    @Override
    public Phase getNextPhase() {
        return new MoveToGround(map);
        
    }

    @Override
    public void updateState(JSONObject response) {
        if (response.has("extras")) {
            JSONObject extras = response.getJSONObject("extras");
            if (extras.has("found")) {
                if ("GROUND".equals(extras.getString("found"))) {
                    groundResponse(true);
                } else {
                    setToFly();
                }
            }
        }
    }

    public boolean isFinal() {
        return false;
    }
}
