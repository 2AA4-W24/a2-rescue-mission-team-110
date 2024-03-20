package ca.mcmaster.se2aa4.island.team110.Phases;

import org.json.JSONArray;
import org.json.JSONObject;

import ca.mcmaster.se2aa4.island.team110.RelativeMap;
import ca.mcmaster.se2aa4.island.team110.Aerial.DroneController;
import ca.mcmaster.se2aa4.island.team110.Aerial.DroneRadar;
import ca.mcmaster.se2aa4.island.team110.Aerial.DroneScanner;
import ca.mcmaster.se2aa4.island.team110.Aerial.DroneHeading;
import ca.mcmaster.se2aa4.island.team110.TileType;

import ca.mcmaster.se2aa4.island.team110.Interfaces.Phase;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class iFirstPass implements Phase {

    private final Logger logger = LogManager.getLogger();
    private DroneController droneController = new DroneController();
    private DroneRadar droneRadar = new DroneRadar();
    private DroneScanner droneScanner = new DroneScanner();

    private DroneHeading previous_direction;

    private RelativeMap map;

    private State current_state = State.SCAN;
    private int turnStage = 0;


    private boolean isOutOfRange = false;
    private boolean hasUturned = false;
    private boolean waitingForEcho = false;
    private int groundDis = -2;
    private String directionToTurn = "";


    private String echohere;

    public iFirstPass(RelativeMap map) {
        this.map = map;
    }

    private enum State {
        ECHO, FLY, SCAN, U_TURN, ECHO2, FLY2
    }

    // private enum Direction {
    //   N, S, E
    // }

    @Override
    public boolean reachedEnd() {
        return isOutOfRange;
    }

    public void determineEcho() {
        if (map.getCurrentHeading() == DroneHeading.NORTH || map.getCurrentHeading() == DroneHeading.SOUTH){
            this.echohere = map.getCurrentHeading() == DroneHeading.NORTH ? "N" : "S";
        } 
        else if (map.getCurrentHeading() == DroneHeading.EAST || map.getCurrentHeading() == DroneHeading.WEST){
            this.echohere = map.getCurrentHeading() == DroneHeading.EAST ? "E" : "W";
        }
    }

    private String makeUTurn() { //Only works for one case (Starting position is top left)
        switch (turnStage) {
            case 0: //check the current heading and determine which direction to turn for the U-turn
                turnStage++;
                this.previous_direction = map.getCurrentHeading();

                if (map.getCurrentHeading() == DroneHeading.SOUTH) { 
                    this.directionToTurn = "E";
                    map.updatePosTurn("LEFT");
                }
                else if (map.getCurrentHeading() == DroneHeading.NORTH) {
                    this.directionToTurn = "E";
                    map.updatePosTurn("RIGHT");
                }
        
                return droneController.turn(directionToTurn);

            case 1: //second stage of the u-turn, we should be in the middle of the u-turn right now
                turnStage++;
                if (this.previous_direction == DroneHeading.SOUTH) {
                    this.directionToTurn = "N";
                    map.updatePosTurn("LEFT");
                
                }
                else if (this.previous_direction == DroneHeading.NORTH) {
                    this.directionToTurn = "S";
                    map.updatePosTurn("RIGHT");
                }
                // else if (this.previous_direction == DroneHeading.EAST) {
                //   this.directionToTurn = "W";
                //   map.updatePosTurn("LEFT");
                // }
                // else if (this.previous_direction == DroneHeading.WEST) {
                //   this.directionToTurn = "E";
                //   map.updatePosTurn("RIGHT");
                // }

                logger.info("Turn: {}", directionToTurn);

                return droneController.turn(directionToTurn);
            case 2: // state to determine if we are at the end of the u-turn to get if we are at the end of scanning
                current_state = State.SCAN;
                turnStage = 0;
                hasUturned = true;
                determineEcho();
                return droneRadar.echo(this.echohere);
            default:
                return null;
        }
    }

    @Override
    public String getNextDecision() {
        logger.info("Phase: iFirstPass");

        if (current_state == State.FLY2 && groundDis > 0) {
            groundDis--;
            logger.error("Flying towards ground, distance left: {}", groundDis);
        }

        switch (current_state) {
            case ECHO:
                current_state = State.SCAN;
                determineEcho();
                return droneRadar.echo(this.echohere);
            case SCAN:
                current_state = State.FLY;
                hasUturned = false;
                return droneScanner.scan();
            case FLY:
                current_state = State.ECHO;
                map.updatePos();
                return droneController.fly();
            case U_TURN:
                return makeUTurn();
            case ECHO2:
                determineEcho();
                return droneRadar.echo(this.echohere);
            case FLY2:
                if (groundDis == 0) {
                    current_state = State.SCAN;
                    groundDis = -2;
                }
                map.updatePos();
                return droneController.fly();
            default:
                return null;
        }
    }

    @Override
    public Phase getNextPhase() {
        return new iSecondPass(this.map);
    }

    @Override
    public void updateState(JSONObject response) {
        if (response.has("extras")) {
            JSONObject extras = response.getJSONObject("extras");
            if (extras.has("found")) {
                if ("OUT_OF_RANGE".equals(extras.getString("found"))) {
                    if (hasUturned) {
                        logger.info("hasUturned is True");
                        isOutOfRange = true;
                    } else {
                        current_state = State.U_TURN;
                    }
                }
            }
            if (extras.has("biomes")) {
                JSONArray biomes = extras.getJSONArray("biomes");
                if (biomes.length() == 1 && "OCEAN".equals(biomes.getString(0))) {
                    waitingForEcho = true;
                    current_state = State.ECHO2;
                }
            }
            if (extras.has("creeks")) {
                JSONArray creeks = extras.getJSONArray("creeks");
                if (!creeks.isEmpty()) {
                    map.addTile(TileType.CREEK);
                    map.addCreekID(creeks.getString(0));
                }
                
            }
            if(extras.has("sites")) {
                JSONArray emergency_site = extras.getJSONArray("sites");
                if (!emergency_site.isEmpty()) {
                    map.addTile(TileType.EMERGENCY_SITE);

                }
                
            }
            if (waitingForEcho && extras.has("range")) {
                groundDis = extras.getInt("range");
                logger.info("Ground distance updated to: {}", groundDis);
                waitingForEcho = false;
                current_state = State.FLY2;
            }
        }
    }
}