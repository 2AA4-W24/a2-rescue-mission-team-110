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

    private boolean special_case = false; //Case for if echoing forward in the beginning detects ground

    private int batteryThreshold = 300;

    public FindGround(RelativeMap map, Battery battery, DefaultJSONResponseParser parser) {
        this.map = map;
        this.battery = battery;
        this.parser = parser;

        this.current_state = State.CHECK_FRONT;
     }

    

    @Override
    public boolean reachedEnd() {
        if (this.goHome) {
            return this.goHome;
        }

        if (this.special_case) {
            return this.special_case;
        }

        return this.turnCompleted;
    }


    @Override
    public String getNextDecision() {

        // if (battery.batteryLevel() < 200) {
        //     this.goHome = true;
        //     return droneController.fly();
        // }

        switch (current_state) {
            case CHECK_FRONT:
                String initialEchoDirection = determineInitialEcho();
                return droneRadar.echo(initialEchoDirection);
                
            case FIND_GROUND:
                String nextEchoDirection = getAndAlternateEchoDirection();
                return droneRadar.echo(nextEchoDirection);

            case TURN_TO_GROUND:
                turnCompleted = true;
                DroneHeading direction = map.getCurrentHeading();
                logger.info("The direction of the drone is {}", direction);
                return droneController.turn(lastEchoDirection);

            case FLY:
                return droneController.fly();

            default:
                return droneController.fly();
        }
    }


    @Override
    public Phase getNextPhase() {
        if (this.goHome) {
            return new ReturnHome(this.map, this.battery);
        }

        return new MoveToGround(this.map, this.battery, this.parser, this.special_case);
    }


    @Override
    public void updateState(JSONObject response) {

        int cost = this.parser.getCost(response);
        this.battery.updateBatteryLevel(cost);


        boolean groundFound = this.parser.echoFound(response);

        if (groundFound && this.current_state == State.CHECK_FRONT) {
            this.special_case = true;
            this.current_state = null;
        }
        else if (groundFound) {
            this.current_state = State.TURN_TO_GROUND;
        }

        else {
            this.current_state = determineNextState();
        }

        if (this.battery.getBatteryLevel() < this.batteryThreshold) {
            this.goHome = true;
        }
    }

    
    @Override
    public boolean isFinal() {
        return false;
    }

    private String determineInitialEcho() {
        DroneHeading initialHeading = map.getCurrentHeading();
        switch(initialHeading) {
            case NORTH:
                return "N";
            case SOUTH:
                return "S";
            case EAST:
                return "E";
            case WEST:
                return "W";
            default:
                return null;
        }
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

    private State determineNextState() {
        switch (this.current_state) {
            case CHECK_FRONT:
                if (this.special_case) {
                    return null;
                }
                return State.FIND_GROUND;
                
            case FIND_GROUND:
                return State.FLY;

            case TURN_TO_GROUND:
                map.updatePosTurn("RIGHT");
                return null;

            case FLY:
                map.updatePos();
                return State.FIND_GROUND;

            default:
                map.updatePos();
                return State.FLY;
        }
    }
}