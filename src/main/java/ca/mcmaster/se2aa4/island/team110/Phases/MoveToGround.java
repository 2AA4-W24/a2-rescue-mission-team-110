package ca.mcmaster.se2aa4.island.team110.Phases;

import org.json.JSONObject;


import ca.mcmaster.se2aa4.island.team110.Aerial.DroneController;
import ca.mcmaster.se2aa4.island.team110.Aerial.DroneRadar;
import ca.mcmaster.se2aa4.island.team110.Aerial.DroneHeading;

import ca.mcmaster.se2aa4.island.team110.RelativeMap;
import ca.mcmaster.se2aa4.island.team110.Records.Battery;
import ca.mcmaster.se2aa4.island.team110.Records.Point;
import ca.mcmaster.se2aa4.island.team110.DefaultJSONResponseParser;

import ca.mcmaster.se2aa4.island.team110.Interfaces.Phase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MoveToGround implements Phase {
    private final Logger logger = LogManager.getLogger();

    private enum State { //States for this phase
        FLY, ECHO;
    }

    //Initialize necessary modules and variables
    private DroneController droneController = new DroneController();
    private DroneRadar droneRadar = new DroneRadar();

    private RelativeMap map;
    private Battery battery;
    private DefaultJSONResponseParser parser;

    private State current_state;
    private int range;

    private boolean reachedGround = false;
    private boolean goHome = false;
    private int batteryThreshold = 300;

    //Constructor
    public MoveToGround(RelativeMap map, Battery battery, DefaultJSONResponseParser parser) {
        this.map = map;
        this.battery = battery;
        this.parser = parser;

        this.current_state = State.ECHO;
        
    }

    @Override
    public boolean reachedEnd() {
        if (this.goHome) { //Insufficient budget
            return goHome;
        }

        return this.reachedGround; //Completed phase
    }

    @Override
    public String getNextDecision() {
        switch (this.current_state) {
            case ECHO:
                String echo_direction = determineEcho();
                return droneRadar.echo(echo_direction);
            case FLY:
                this.map.updatePos();
                this.range--;
                return droneController.fly();
            default:
                return null;
        }
    }

    @Override
    public Phase getNextPhase() {
        if (this.goHome) {
            return new ReturnHome(this.map, this.battery);
        }
        return new iFirstPass(this.map, this.battery, this.parser);
        
    }

    public void updateState(JSONObject response) {

        int cost = this.parser.getCost(response);
        this.battery.updateBatteryLevel(cost);

        if (this.parser.echoRange(response) != Integer.MAX_VALUE) {
            this.range = this.parser.echoRange(response);
        }
     
        if (range == 1) {

            this.reachedGround = true;
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

    private String determineEcho() {
        String echo_direction = "";
        if (map.getCurrentHeading() == DroneHeading.NORTH || map.getCurrentHeading() == DroneHeading.SOUTH) {
            echo_direction = map.getCurrentHeading() == DroneHeading.NORTH ? "N" : "S";
        } 
        else if (map.getCurrentHeading() == DroneHeading.EAST || map.getCurrentHeading() == DroneHeading.WEST) {
            echo_direction = map.getCurrentHeading() == DroneHeading.EAST ? "E" : "W";
        }
        return echo_direction;
    } 
    
    private State determineNextState() {
        switch(this.current_state) {
            case ECHO:
                return State.FLY;
            case FLY:
                return State.FLY;
            default: 
                return null;
      
        }

    }
}