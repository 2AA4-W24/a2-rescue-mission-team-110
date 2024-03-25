package ca.mcmaster.se2aa4.island.team110.Phases;

import org.json.JSONArray;
import org.json.JSONObject;


import ca.mcmaster.se2aa4.island.team110.TileType;
import ca.mcmaster.se2aa4.island.team110.Aerial.DroneController;
import ca.mcmaster.se2aa4.island.team110.Aerial.DroneRadar;
import ca.mcmaster.se2aa4.island.team110.Aerial.DroneScanner;
import ca.mcmaster.se2aa4.island.team110.Aerial.DroneHeading;

import ca.mcmaster.se2aa4.island.team110.RelativeMap;
import ca.mcmaster.se2aa4.island.team110.Records.Battery;
import ca.mcmaster.se2aa4.island.team110.DefaultJSONResponseParser;

import ca.mcmaster.se2aa4.island.team110.Interfaces.Phase;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class iSecondPass implements Phase {

    private final Logger logger = LogManager.getLogger();
    private DroneController droneController = new DroneController();
    private DroneRadar droneRadar = new DroneRadar();
    private DroneScanner droneScanner = new DroneScanner();
    private DroneHeading previous_direction;
    private DroneHeading initial_direction;

    private RelativeMap map;
    private Battery battery;
    private DefaultJSONResponseParser parser;

    private State current_state;
    private int initTurnStage = 0;
    private int turnStage = -1;

    private boolean isOutOfRange = false;
    private boolean outOfRange = false;
    private boolean uTurnOk = false;
    private boolean hasUturned = false;
    private boolean okToEchoFoward = false;
    private boolean clearGround = false;
    private boolean canClearGround = false;
    private boolean duringInitialUturn = true;
    private boolean hasInitUTurned = false;
    private int groundDis = -1;
    private String directionToTurn = "";


    private String echohere;
    private String uturnechohere;

    private boolean goHome = false;

    private int batteryThreshold = 300;

    public iSecondPass(RelativeMap map, Battery battery, DefaultJSONResponseParser parser) {
        this.map = map;
        this.battery = battery;
        this.parser = parser;

        this.current_state = State.INIT_U_TURN;

    }

    private enum State {
        ECHO, FLY, SCAN, INIT_U_TURN, U_TURN, FLY2; 
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
        logger.info("Phase: iSecondPass");

        switch (current_state) {
            case ECHO:
                determineEcho();
                return droneRadar.echo(this.echohere);
            case SCAN:
                hasUturned = false;
                return droneScanner.scan();
            case FLY:
                map.updatePos();
                return droneController.fly();
            case INIT_U_TURN:
                return initialUTurn();
            case U_TURN:
                return makeUTurn();
            case FLY2:
                if (this.groundDis > 0) {
                    groundDis--;
                }
                if (groundDis == 0) {
                    groundDis = -1;
                    map.updatePos();
                    return droneController.fly();
                }
                map.updatePos();
                return droneController.fly();
            default:
                return null;
        }

    }

    @Override
    public Phase getNextPhase() {
        if (this.goHome) {
            return new ReturnHome(map, battery);
        }
        else {
            return new ReturnHome(map, battery);
        }
       
    }

    @Override
    public void updateState(JSONObject response) {

        int cost = this.parser.getCost(response);
        this.battery.updateBatteryLevel(cost);

    
        this.current_state = determineNextState();
    

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
                    } else if (duringInitialUturn) {
                        outOfRange = true;
                        current_state = State.INIT_U_TURN;
                    } else {
                        okToEchoFoward = false;
                        outOfRange = true;
                        current_state = State.U_TURN;
                    }
                } else {
                    outOfRange = false;
                }
            }
            if (okToEchoFoward && extras.has("range")) {
                groundDis = extras.getInt("range");
                if (current_state != State.FLY2 && (groundDis > 0)) {
                    logger.info("Ground distance updated to: {}", groundDis);
                    current_state = State.FLY2;
                }
            }
            if (canClearGround && extras.has("range")) { //Was an optimization but it does not work for map 17 for full coverage, maybe should omit
                clearGround = (extras.getInt("range") > 15);
                current_state = State.U_TURN;
            }
            if (duringInitialUturn && extras.has("range")) {
                uTurnOk = (extras.getInt("range") > 5);
                current_state = State.INIT_U_TURN;
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
            this.uturnechohere = map.getCurrentHeading() == DroneHeading.NORTH ? "W" : "W"; // Change logic for all
                                                                                            // cases
        } else if (map.getCurrentHeading() == DroneHeading.EAST || map.getCurrentHeading() == DroneHeading.WEST) {
            this.echohere = map.getCurrentHeading() == DroneHeading.EAST ? "E" : "W";
        }
    }

    private String initialUTurn() {
        switch (initTurnStage) {
            case 0:
                initTurnStage++;
                logger.info("initialUTurn: case 0");
                this.initial_direction = map.getCurrentHeading();
                if (map.getCurrentHeading() == DroneHeading.NORTH) {
                    this.directionToTurn = "W";
                    map.updatePosTurn("LEFT");
                } else if (map.getCurrentHeading() == DroneHeading.SOUTH) {
                    this.directionToTurn = "W";
                    map.updatePosTurn("RIGHT");
                }
                return droneController.turn(directionToTurn);

            case 1:
                logger.info("initialUTurn: case 1");

                initTurnStage++;
                if (this.initial_direction == DroneHeading.NORTH) {
                    this.directionToTurn = "S";
                    map.updatePosTurn("LEFT");
                } else if (this.initial_direction == DroneHeading.SOUTH) {
                    this.directionToTurn = "N";
                    map.updatePosTurn("RIGHT");
                }
                return droneController.turn(directionToTurn);
            case 2:
                logger.info("initialUTurn: case 2");

                initTurnStage++;
                determineEcho();
                return droneRadar.echo(this.uturnechohere);
            case 3:
                logger.info("initialUTurn: case 3");

                if (outOfRange || uTurnOk) {
                    initTurnStage++;
                } else {
                    initTurnStage = 2;
                    map.updatePos();
                    return droneController.fly();
                }
            case 4:
                logger.info("initialUTurn: case 4");

                initTurnStage++;
                if (this.initial_direction == DroneHeading.NORTH) {
                    this.directionToTurn = "W";
                    map.updatePosTurn("RIGHT");
                } else if (this.initial_direction == DroneHeading.SOUTH) {
                    this.directionToTurn = "W";
                    map.updatePosTurn("LEFT");
                }
                return droneController.turn(directionToTurn);
            case 5:
                logger.info("initialUTurn: case 5");

                initTurnStage++;
                if (this.initial_direction == DroneHeading.NORTH) {
                    this.directionToTurn = "S";
                    map.updatePosTurn("LEFT");
                } else if (this.initial_direction == DroneHeading.SOUTH) {
                    this.directionToTurn = "N";
                    map.updatePosTurn("RIGHT");
                }
                return droneController.turn(directionToTurn);
            case 6:
                logger.info("initialUTurn: case 6");

                initTurnStage++;
                if (this.initial_direction == DroneHeading.NORTH) {
                    this.directionToTurn = "E";
                    map.updatePosTurn("LEFT");
                } else if (this.initial_direction == DroneHeading.SOUTH) {
                    this.directionToTurn = "E";
                    map.updatePosTurn("RIGHT");
                }
                return droneController.turn(directionToTurn);
            case 7:
                initTurnStage++;
                map.updatePos();
                return droneController.fly();
            case 8:
                initTurnStage++;
                if (this.initial_direction == DroneHeading.NORTH) {
                    this.directionToTurn = "N";
                    map.updatePosTurn("LEFT");
                } else if (this.initial_direction == DroneHeading.SOUTH) {
                    this.directionToTurn = "S";
                    map.updatePosTurn("RIGHT");
                }
                return droneController.turn(directionToTurn);
            case 9:
                logger.info("initialUTurn: case 7");

                initTurnStage++;
                determineEcho();
                return droneRadar.echo(this.echohere);
            case 10:
                logger.info("initialUTurn: case 8");

                if (outOfRange) {
                    if (this.initial_direction == DroneHeading.NORTH) {
                        initTurnStage++;
                        this.directionToTurn = "W";
                        map.updatePosTurn("LEFT");
                        return droneController.turn(directionToTurn);
                    } else if (this.initial_direction == DroneHeading.SOUTH) {
                        initTurnStage++;
                        this.directionToTurn = "W";
                        map.updatePosTurn("RIGHT");
                        return droneController.turn(directionToTurn);
                    }
                } else {
                    duringInitialUturn = false;
                    okToEchoFoward = true;
                    this.hasInitUTurned = true;
                    map.updatePos();
                    return droneController.fly();
                }
            case 11:
                logger.info("initialUTurn: case 9");

                initTurnStage++;
                if (this.initial_direction == DroneHeading.NORTH) {
                    this.directionToTurn = "N";
                    map.updatePosTurn("RIGHT");
                } else if (this.initial_direction == DroneHeading.SOUTH) {
                    this.directionToTurn = "S";
                    map.updatePosTurn("LEFT");
                }
                okToEchoFoward = true;
                duringInitialUturn = false;
                this.hasInitUTurned = true;
                return droneController.turn(directionToTurn);
            default:
                return null;
        }
    }

    private String makeUTurn() {
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
                } else {
                    turnStage = 0;
                    map.updatePos();
                    return droneController.fly();
                }
            case 2:
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

            case 3:
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
            case 4:
                current_state = State.SCAN;
                turnStage = -1;
                hasUturned = true;
                canClearGround = false;
                okToEchoFoward = true;
                determineEcho();
                return droneRadar.echo(this.echohere); // we need a method that determines the heading for u
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
            case INIT_U_TURN:
                if (this.hasInitUTurned) {
                    return State.ECHO;
                }
                return State.INIT_U_TURN;
            case U_TURN:
                if (this.hasUturned) {
                    return State.SCAN;
                }
                return State.U_TURN;
            case FLY2:
                if (groundDis == -1) {
                    return State.SCAN;
                }
                return State.FLY2;
            default:
                return null;

        }

    }

    
}