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


    public FindGround(RelativeMap map) { //Constructor to initialize this instance
        this.map = map;
        this.current_state = State.FIND_GROUND;
     }


    private String getAndAlternateEchoDirection() { // Method to detrmine which direction to echo depending on which side we start on
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
    public boolean reachedEnd() { //Method to check if we've reached the end of the phase
        return turnCompleted;
    }

    @Override
    public String getNextDecision() { //Method to determine what hte next decision is depending on specific conditions
        logger.info("Phase: FindGround"); //Debugging Log (remove later)

        switch (current_state) { //Determining what to do next depending on what the current state is 
            case FIND_GROUND:
                String nextEchoDirection = getAndAlternateEchoDirection(); // Echoing to check if ground is Left/Right of the drone
                current_state = State.FLY;
                return droneRadar.echo(nextEchoDirection);
            case GO_TO_GROUND:
                current_state = State.FLY; 
                turnCompleted = true; //We need to add cases for if we start in the center of one of the edges of the map (remove later)
                map.updatePosTurn("RIGHT"); //Begin to update the relative map depending on the move

                DroneHeading direction = map.getCurrentHeading(); //Debugging logs (remove later)
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

    public void groundResponse(boolean groundFound) { //Method for checking whether or not ground has been found in the last state, used in updateState
        groundDetected = groundFound;
        if (groundDetected) {
            current_state = State.GO_TO_GROUND;
        } else {
            current_state = State.FLY;
        }
    }

    @Override
    public Phase getNextPhase() { //Method to go to the next phase once we've finished with this one
        return new MoveToGround(map); //Next phase is moving to ground (wait did raymond basically remove the movetoground phase) (remove later)
    }

    @Override
    public void updateState(JSONObject response) { //Method to update the state depending on what scan returns
        if (response.has("extras")) {
            JSONObject extras = response.getJSONObject("extras");
            if (extras.has("found")) {  //Checks if echo responds with a "found" JSONObject
                if ("GROUND".equals(extras.getString("found"))) {
                    groundResponse(true);
                } else {
                    current_state = State.FLY;
                }
            }
        }
    }

    @Override
    public boolean isFinal() {
        return false;
    }
}
