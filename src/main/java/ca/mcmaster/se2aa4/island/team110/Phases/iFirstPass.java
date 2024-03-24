package ca.mcmaster.se2aa4.island.team110.Phases;

import org.json.JSONArray;
import org.json.JSONObject;


import ca.mcmaster.se2aa4.island.team110.Aerial.DroneController;
import ca.mcmaster.se2aa4.island.team110.Aerial.DroneRadar;
import ca.mcmaster.se2aa4.island.team110.Aerial.DroneScanner;
import ca.mcmaster.se2aa4.island.team110.Aerial.DroneHeading;
import ca.mcmaster.se2aa4.island.team110.TileType;

import ca.mcmaster.se2aa4.island.team110.Interfaces.Phase;
import ca.mcmaster.se2aa4.island.team110.RelativeMap;
import ca.mcmaster.se2aa4.island.team110.Records.Battery;
import ca.mcmaster.se2aa4.island.team110.DefaultJSONResponseParser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class iFirstPass implements Phase {

    private final Logger logger = LogManager.getLogger();
    private DroneController droneController = new DroneController();
    private DroneRadar droneRadar = new DroneRadar();
    private DroneScanner droneScanner = new DroneScanner();

    private DroneHeading previous_direction;

    private RelativeMap map;
    private Battery battery;
    private DefaultJSONResponseParser parser;

    private boolean special_case;


    private State current_state = State.SCAN;
    private int turnStage = -1;

    private boolean isOutOfRange = false;
    private boolean outOfRange = false;
    private boolean hasUturned = false;
    private boolean clearGround = false;
    private boolean canClearGround = false;
    private boolean okToEchoFoward = true;
    private int groundDis = -1;
    private String directionToTurn = "";

    private String echohere;
    private String uturnechohere;
    private boolean goHome = false;

    private int batteryThreshold = 300;

    public iFirstPass(RelativeMap map, Battery battery, DefaultJSONResponseParser parser, boolean special_case) {
        this.map = map;
        this.battery = battery;
        this.parser = parser;
        this.special_case = special_case;
    }

    private enum State {
        ECHO, FLY, SCAN, U_TURN, FLY2, GO_HOME;
    }


    @Override
    public boolean reachedEnd() {
        if (goHome) {
            return goHome;
        }
        return isOutOfRange;
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
                current_state = State.FLY;
                determineEcho();
                return droneRadar.echo(this.echohere);
            case SCAN:
                current_state = State.ECHO;
                hasUturned = false;
                return droneScanner.scan();
            case FLY:
                current_state = State.SCAN;
                this.map.updatePos();
                return droneController.fly();
            case U_TURN:
                return makeUTurn();
            // case ECHO2:
            // determineEcho();
            // return droneRadar.echo(this.echohere);
            case FLY2:
                if (groundDis == 0) {
                    current_state = State.SCAN;
                    groundDis = -1;
                    this.map.updatePos();
                    return droneController.fly();
                } 
                this.map.updatePos();
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
        else {
            return new iSecondPass(this.map, this.battery, this.parser);
        }
    }

    @Override
    public void updateState(JSONObject response) {

        int cost = this.parser.getCost(response);
        this.battery.updateBatteryLevel(cost);


        if (this.parser.scanTile(response) != null) {
            TileType tile = this.parser.scanTile(response);
            map.addTile(tile);
        }
        

        if (this.parser.getID(response) != null) {
            JSONArray id = this.parser.getID(response);
            map.addCreekID(id.getString(0));
        }
        
        if (response.has("extras")) {
            JSONObject extras = response.getJSONObject("extras");
            if (extras.has("found")) {
                if ("OUT_OF_RANGE".equals(extras.getString("found"))) {
                    if (hasUturned) {
                        logger.info("hasUturned is True");
                        isOutOfRange = true;
                    } else {
                        okToEchoFoward = false;
                        outOfRange = true;
                        this.current_state = State.U_TURN;
                    }
                } else {
                    outOfRange = false;
                }
            }
    
            if (okToEchoFoward && extras.has("range")) {
                groundDis = extras.getInt("range");
                if (this.current_state != State.FLY2 && (groundDis > 0)) {
                    logger.info("Ground distance updated to: {}", groundDis);
                    this.current_state = State.FLY2;
                }
            }
            if (canClearGround && extras.has("range")) {  //Was an optimization but it does not work for map 17 for full coverage, maybe should omit
                clearGround = (extras.getInt("range") > 15);
                this.current_state = State.U_TURN;
            }
        }

        if (this.battery.getBatteryLevel() < this.batteryThreshold) {
            this.goHome = true;
        }
    }

    @Override
    public boolean isFinal() {
        return false;
    }
    
    public void determineEcho() {
        if (map.getCurrentHeading() == DroneHeading.NORTH || map.getCurrentHeading() == DroneHeading.SOUTH) {
            this.echohere = map.getCurrentHeading() == DroneHeading.NORTH ? "N" : "S";
            this.uturnechohere = map.getCurrentHeading() == DroneHeading.NORTH ? "E" : "E"; // Change logic for all
                                                                                            // cases
        } else if (map.getCurrentHeading() == DroneHeading.EAST || map.getCurrentHeading() == DroneHeading.WEST) {
            this.echohere = map.getCurrentHeading() == DroneHeading.EAST ? "E" : "W";
        }
    }

    private String makeUTurn() { // Only works for one case (Starting position is top left)
        switch (turnStage) {
            case -1:
                turnStage++;
                return droneScanner.scan();
            case 0:
                turnStage++;
                canClearGround = true;
                determineEcho();
                return droneRadar.echo(this.uturnechohere);
            case 1:
                if (outOfRange || clearGround) {
                    turnStage++;
                } 
                else {
                    turnStage = 0;
                    map.updatePos();
                    return droneController.fly();
                }
            case 2: // check the current heading and determine which direction to turn for the
                    // U-turn
                turnStage++;
                this.previous_direction = map.getCurrentHeading();

                if (map.getCurrentHeading() == DroneHeading.SOUTH) {
                    this.directionToTurn = "E";
                    map.updatePosTurn("LEFT");
                } else if (map.getCurrentHeading() == DroneHeading.NORTH) {
                    this.directionToTurn = "E";
                    map.updatePosTurn("RIGHT");
                }

                return droneController.turn(directionToTurn);

            case 3: // second stage of the u-turn, we should be in the middle of the u-turn right
                    // now
                turnStage++;
                if (this.previous_direction == DroneHeading.SOUTH) {
                    this.directionToTurn = "N";
                    map.updatePosTurn("LEFT");

                } else if (this.previous_direction == DroneHeading.NORTH) {
                    this.directionToTurn = "S";
                    map.updatePosTurn("RIGHT");
                }
                // else if (this.previous_direction == DroneHeading.EAST) {
                // this.directionToTurn = "W";
                // map.updatePosTurn("LEFT");
                // }
                // else if (this.previous_direction == DroneHeading.WEST) {
                // this.directionToTurn = "E";
                // map.updatePosTurn("RIGHT");
                // }

                logger.info("Turn: {}", directionToTurn);

                return droneController.turn(directionToTurn);
            case 4: // state to determine if we are at the end of the u-turn to get if we are at the
                    // end of scanning
                current_state = State.SCAN;
                turnStage = -1;
                hasUturned = true;
                canClearGround = false;
                okToEchoFoward = true;
                determineEcho();
                return droneRadar.echo(this.echohere);
            default:
                return null;
        }
    }

    private State determineNextState() {
        switch(this.current_state) {
            case ECHO:
                return State.FLY;
            case SCAN:
                return State.ECHO;
            case FLY:
                return State.SCAN;
            case U_TURN:
                return State.SCAN;
            case FLY2:
                if (this.groundDis == -1) {
                    return State.SCAN;
                }
                return State.FLY2;
            default:
                return null;
        }
    }


}