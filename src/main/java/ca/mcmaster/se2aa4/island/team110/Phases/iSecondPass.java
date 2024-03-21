package ca.mcmaster.se2aa4.island.team110.Phases;

import org.json.JSONArray;
import org.json.JSONObject;

import ca.mcmaster.se2aa4.island.team110.RelativeMap;
import ca.mcmaster.se2aa4.island.team110.TileType;
import ca.mcmaster.se2aa4.island.team110.Aerial.DroneController;
import ca.mcmaster.se2aa4.island.team110.Aerial.DroneRadar;
import ca.mcmaster.se2aa4.island.team110.Aerial.DroneScanner;
import ca.mcmaster.se2aa4.island.team110.Aerial.DroneHeading;

import ca.mcmaster.se2aa4.island.team110.Interfaces.Phase;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class iSecondPass implements Phase {

    private final Logger logger = LogManager.getLogger();
    private DroneController droneController = new DroneController();
    private DroneRadar droneRadar = new DroneRadar();
    private DroneScanner droneScanner = new DroneScanner();

    private DroneHeading initial_direction;

    private RelativeMap map;

    private State current_state = State.INIT_U_TURN;
    private int initTurnStage = 0;
    private int turnStage = 0;

    private boolean isOutOfRange = false;
    private boolean hasUturned = false;
    private boolean waitingForEcho = false;
    private int groundDis = -2;
    private String directionToTurn = "";
    

    private String echohere;

    public iSecondPass(RelativeMap map) {
        this.map = map;

    }

    private enum State {
        ECHO, FLY, SCAN, INIT_U_TURN, U_TURN, ECHO2, FLY2; 
    }

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

    private String initialUTurn() {
        switch (initTurnStage) {
            case 0:
                initTurnStage++;
                this.initial_direction = map.getCurrentHeading();
                if (map.getCurrentHeading() == DroneHeading.NORTH) {
                    this.directionToTurn = "E";
                    map.updatePosTurn("RIGHT");
                } else if (map.getCurrentHeading() == DroneHeading.SOUTH) {
                    this.directionToTurn = "E";
                    map.updatePosTurn("LEFT");
                }
                
                return droneController.turn(directionToTurn);

            case 1:
                initTurnStage++;
                if (this.initial_direction == DroneHeading.NORTH) {
                    this.directionToTurn = "S";
                    map.updatePosTurn("RIGHT");
                } else if (this.initial_direction == DroneHeading.SOUTH) {
                    this.directionToTurn = "N";
                    map.updatePosTurn("LEFT");
                }
                
                return droneController.turn(directionToTurn);

            case 2:
                initTurnStage++;
                if (this.initial_direction == DroneHeading.NORTH) {
                    this.directionToTurn = "W";
                    map.updatePosTurn("RIGHT");
                } else if (this.initial_direction == DroneHeading.SOUTH) {
                    this.directionToTurn = "E";
                    map.updatePosTurn("RIGHT");
                }

                return droneController.turn(directionToTurn);

            case 3:
                initTurnStage++;
                map.updatePos();
                return droneController.fly();

            case 4:
                initTurnStage = 0;
                if (this.initial_direction == DroneHeading.NORTH) {
                    this.directionToTurn = "N";
                    map.updatePosTurn("RIGHT");
                } else if (this.initial_direction == DroneHeading.SOUTH) {
                    this.directionToTurn = "S";
                    map.updatePosTurn("LEFT");
                }
               
                current_state = State.SCAN;
                hasUturned = true;
                return droneController.turn(directionToTurn);
            default:
                return null;
        }
    }

    private String makeUTurn() {
        switch (turnStage) {
            case 0:
                turnStage++;
                this.initial_direction = map.getCurrentHeading();
                if (map.getCurrentHeading() == DroneHeading.SOUTH) {
                    this.directionToTurn = "W";
                    map.updatePosTurn("RIGHT");
                } else if (map.getCurrentHeading() == DroneHeading.NORTH) {
                    this.directionToTurn = "W";
                    map.updatePosTurn("LEFT");
                }

                return droneController.turn(directionToTurn);

            case 1:
                turnStage++;
                if (this.initial_direction == DroneHeading.SOUTH) {
                    this.directionToTurn = "N";
                    map.updatePosTurn("RIGHT");

                } else if (this.initial_direction == DroneHeading.NORTH) {
                    this.directionToTurn = "S";
                    map.updatePosTurn("LEFT");
                }

                logger.info("Turn: {}", directionToTurn);

                return droneController.turn(directionToTurn);
            case 2:
                current_state = State.SCAN;
                turnStage = 0;
                hasUturned = true;
                determineEcho();
                return droneRadar.echo(this.echohere); //we need a method that determines the heading for u
            default:
                return null;
        }
    }

    @Override
    public String getNextDecision() {
        logger.info("Phase: iSecondPass");

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
                map.updatePos();
                current_state = State.ECHO;
                return droneController.fly();
            case INIT_U_TURN:
                return initialUTurn();
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
        logger.info("Second pass ended");
        return new ReturnHome(this.map);
    }

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
                if (biomes.length() == 1 && "OCEAN".equals(biomes.getString(0)) && !hasUturned) {
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

    public boolean isFinal() {
        return false;
    }
}