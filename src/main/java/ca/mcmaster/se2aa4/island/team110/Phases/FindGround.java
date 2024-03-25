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
import ca.mcmaster.se2aa4.island.team110.Records.Battery;
import ca.mcmaster.se2aa4.island.team110.DefaultJSONResponseParser;

public class FindGround implements Phase {
    private final Logger logger = LogManager.getLogger();

    private enum State {
        CHECK_FRONT, FIND_GROUND, TURN_TO_GROUND, FLY;
    }

    private DroneController droneController = new DroneController();
    private DroneRadar droneRadar = new DroneRadar();

    private State current_state;


    private RelativeMap map;
    private Battery battery;
    private DefaultJSONResponseParser parser;


    private String lastEchoDirection = null;
    private boolean turnCompleted = false;
    private boolean goHome = false;

    private int batteryThreshold = 300;

    //Constructor 
    public FindGround(RelativeMap map, Battery battery, DefaultJSONResponseParser parser) {
        this.map = map;
        this.battery = battery;
        this.parser = parser;

        this.current_state = State.FIND_GROUND;
     }

    
    @Override
    public boolean reachedEnd() { //Conditions to determine when we reached the end of the phase
        if (this.goHome) { //Condition 1: Insufficient budget to keep progressing
            return this.goHome;
        }

        return this.turnCompleted; //Condition 2: Completed the turn to move onto the MoveToGround
    }


    @Override
    public String getNextDecision() { //Getting the next decision in the phase
        switch (current_state) { //Dependent on the current_state
            case FIND_GROUND: //Finding Ground
                String nextEchoDirection = getAndAlternateEchoDirection();
                return droneRadar.echo(nextEchoDirection);
            case TURN_TO_GROUND: //Turning to Ground
                map.updatePosTurn("RIGHT"); 
                turnCompleted = true;
                return droneController.turn(lastEchoDirection);
            case FLY: //Fly forward 
                map.updatePos();
                return droneController.fly();
            default:
                map.updatePos();
                return droneController.fly();
        }
    }


    @Override
    public Phase getNextPhase() { //Conditons to get next phase
        if (this.goHome) { //If the drone has has insufficient budget to keep progressing, just go home
            return new ReturnHome(this.map, this.battery);
        }

        return new MoveToGround(this.map, this.battery, this.parser); //Completed phase will move on to MoveToGround
    }
   
    @Override
    public void updateState(JSONObject response) { //Updating the state based on previous decision outcomes

        //Updating battery tracker
        int cost = this.parser.getCost(response);
        this.battery.updateBatteryLevel(cost);

        //Checks if the previous echo found ground
        boolean groundFound = this.parser.echoFound(response); 
        if (groundFound) { //If the drone has found ground, it will proceed to go to it
            this.current_state = State.TURN_TO_GROUND;
        }
        else { 
            this.current_state = determineNextState();
        }

        //Checks if the drone doesn't have enough battery to keep going
        if (this.battery.getBatteryLevel() < this.batteryThreshold) {
            this.goHome = true;
        }
    }

    
    @Override
    public boolean isFinal() { //Boolean to determine if this phase is the final phase
        return false;
    }

    private String getAndAlternateEchoDirection() { //Alternating echo scans
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

    private State determineNextState() { //logic to determine the next state
        switch (this.current_state) {
            case FIND_GROUND:
                return State.FLY;

            case TURN_TO_GROUND:
                return null;

            case FLY:
                return State.FIND_GROUND;

            default:
                return State.FLY;
        }
    }
}