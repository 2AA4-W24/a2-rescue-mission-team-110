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
    private DroneHeading previous_direction;
    private DroneHeading initial_direction;

    private RelativeMap map;

    private State current = State.INIT_U_TURN;
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
    private int groundDis = -1;
    private String directionToTurn = "";
    private String mapDirUpdate = "";

    private String echohere;
    private String uturnechohere;

    public iSecondPass(RelativeMap map) {
        this.map = map;

    }

    private enum State {
        ECHO, FLY, SCAN, INIT_U_TURN, U_TURN, FLY2, STOP; // stop state is a placeholder, used for debugging
    }

    @Override
    public boolean reachedEnd() {
        return isOutOfRange;
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
                    current = State.ECHO;
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
                current = State.ECHO;
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
                current = State.SCAN;
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

    @Override
    public String getNextDecision() {
        logger.info("Phase: iSecondPass");

        if (current == State.FLY2 && groundDis > 0) {
            groundDis--;
            logger.error("Flying towards ground, distance left: {}", groundDis);
        }

        switch (current) {
            case ECHO:
                current = State.FLY;
                determineEcho();
                return droneRadar.echo(this.echohere);
            case SCAN:
                current = State.ECHO;
                hasUturned = false;
                return droneScanner.scan();
            case FLY:
                map.updatePos();
                current = State.SCAN;
                return droneController.fly();
            case INIT_U_TURN:
                return initialUTurn();
            case U_TURN:
                return makeUTurn();
            case FLY2:
                if (groundDis == 0) {
                    current = State.SCAN;
                    groundDis = -1;
                    map.updatePos();
                    return droneController.fly();
                }
                map.updatePos();
                return droneController.fly();
            case STOP:
                return droneController.stop();
            default:
                return null;
        }

    }

    @Override
    public Phase getNextPhase() {
        logger.info("Second pass ended");
        return new ReturnHome(map);
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
                    } else if (duringInitialUturn) {
                        outOfRange = true;
                        current = State.INIT_U_TURN;
                    } else {
                        okToEchoFoward = false;
                        outOfRange = true;
                        current = State.U_TURN;
                    }
                } else {
                    outOfRange = false;
                }
            }
            // if (extras.has("biomes")) {
            // JSONArray biomes = extras.getJSONArray("biomes");
            // if (okToEchoFoward && biomes.length() == 1 &&
            // "OCEAN".equals(biomes.getString(0)) && !hasUturned) {
            // waitingForEcho = true;
            // current = State.ECHO2;
            // }
            // }
            if (extras.has("creeks")) {
                JSONArray creeks = extras.getJSONArray("creeks");
                if (!creeks.isEmpty()) {
                    map.addTile(TileType.CREEK);
                    map.addCreekID(creeks.getString(0));
                }
            }
            if (extras.has("sites")) {
                JSONArray emergency_site = extras.getJSONArray("sites");
                if (!emergency_site.isEmpty()) {
                    map.addTile(TileType.EMERGENCY_SITE);
                }
            }
            if (okToEchoFoward && extras.has("range")) {
                groundDis = extras.getInt("range");
                if (current != State.FLY2 && (groundDis > 0)) {
                    logger.info("Ground distance updated to: {}", groundDis);
                    current = State.FLY2;
                }
            }
            if (canClearGround && extras.has("range")) { //Was an optimization but it does not work for map 17 for full coverage, maybe should omit
                clearGround = (extras.getInt("range") > 15);
                current = State.U_TURN;
            }
            if (duringInitialUturn && extras.has("range")) {
                uTurnOk = (extras.getInt("range") > 5);
                current = State.INIT_U_TURN;
            }
        }
    }

    @Override
    public boolean isFinal() {
        return false;
    }
}