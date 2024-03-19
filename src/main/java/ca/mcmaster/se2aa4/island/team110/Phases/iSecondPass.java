package ca.mcmaster.se2aa4.island.team110.Phases;

import org.json.JSONArray;
import org.json.JSONObject;

import ca.mcmaster.se2aa4.island.team110.RelativeMap;
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
    private DroneHeading currDir;
    private DroneHeading previousDir;
    private DroneHeading initDir;

    private RelativeMap map;

    private State current = State.INIT_U_TURN;
    private int initTurnStage = 0;
    private int turnStage = 0;

    private boolean isOutOfRange = false;
    private boolean hasUturned = false;
    private boolean waitingForEcho = false;
    private int groundDis = -2;
    private String directionToTurn = "";
    private String mapDirUpdate = "";

    public iSecondPass(RelativeMap map, DroneHeading direction) {
        this.map = map;
        this.currDir = direction;

    }

    private enum State {
        ECHO, FLY, SCAN, INIT_U_TURN, U_TURN, ECHO2, FLY2
    }

    public boolean reachedEnd() {
        return isOutOfRange;
    }

    private String initialUTurn() {
        switch (initTurnStage) {
            case 0:
                initTurnStage++;
                if (this.currDir == DroneHeading.NORTH) {
                    this.directionToTurn = "E";
                } else if (this.currDir == DroneHeading.SOUTH) {
                    this.directionToTurn = "E";
                }
                this.mapDirUpdate = "RIGHT";
                this.initDir = this.currDir;
                this.currDir = this.currDir.turn(this.mapDirUpdate);
                return droneController.turn(directionToTurn);

            case 1:
                initTurnStage++;
                if (this.initDir == DroneHeading.NORTH) {
                    this.directionToTurn = "S";
                } else if (this.initDir == DroneHeading.SOUTH) {
                    this.directionToTurn = "N";
                }
                this.mapDirUpdate = "RIGHT";
                this.currDir = this.currDir.turn(this.mapDirUpdate);
                return droneController.turn(directionToTurn);

            case 2:
                initTurnStage++;
                if (this.initDir == DroneHeading.NORTH) {
                    this.directionToTurn = "W";
                } else if (this.initDir == DroneHeading.SOUTH) {
                    this.directionToTurn = "E";
                }
                this.mapDirUpdate = "RIGHT";
                this.currDir = this.currDir.turn(this.mapDirUpdate);
                return droneController.turn(directionToTurn);

            case 3:
                initTurnStage++;
                return droneController.fly();

            case 4:
                initTurnStage = 0;
                if (this.initDir == DroneHeading.NORTH) {
                    this.directionToTurn = "N";
                } else if (this.initDir == DroneHeading.SOUTH) {
                    this.directionToTurn = "S";
                }
                this.mapDirUpdate = "RIGHT";
                current = State.SCAN;
                hasUturned = true;
                this.currDir = this.currDir.turn(this.mapDirUpdate);
                return droneController.turn(directionToTurn);
            default:
                return null;
        }
    }

    private String makeUTurn() {
        switch (turnStage) {
            case 0:
                turnStage++;
                if (this.currDir == DroneHeading.SOUTH) {
                    this.directionToTurn = "W";
                    this.mapDirUpdate = "RIGHT";
                } else if (this.currDir == DroneHeading.NORTH) {
                    this.directionToTurn = "W";
                    this.mapDirUpdate = "LEFT";
                }

                this.initDir = this.currDir;
                this.currDir = this.currDir.turn(this.mapDirUpdate);
                return droneController.turn(directionToTurn);

            case 1:
                turnStage++;
                if (this.initDir == DroneHeading.SOUTH) {
                    this.directionToTurn = "N";
                    this.mapDirUpdate = "RIGHT";

                } else if (this.initDir == DroneHeading.NORTH) {
                    this.directionToTurn = "S";
                    this.mapDirUpdate = "LEFT";
                }

                this.currDir = this.currDir.turn(this.mapDirUpdate);
                logger.info("Turn: {}", directionToTurn);

                return droneController.turn(directionToTurn);
            case 2:
                current = State.SCAN;
                turnStage = 0;
                hasUturned = true;
                return droneRadar.echo(currDir == DroneHeading.NORTH ? "N" : "S");
            default:
                return null;
        }
    }

    public String getNextDecision() {
        logger.info("Phase: iSecondPass");

        if (current == State.FLY2 && groundDis >= 0) {
            groundDis--;
            logger.error("Flying towards ground, distance left: {}", groundDis);
        }

        switch (current) {
            case ECHO:
                current = State.SCAN;
                return droneRadar.echo(currDir == DroneHeading.NORTH ? "N" : "S");
            case SCAN:
                current = State.FLY;
                hasUturned = false;
                return droneScanner.scan();
            case FLY:
                current = State.ECHO;
                return droneController.fly();
            case INIT_U_TURN:
                return initialUTurn();
            case U_TURN:
                return makeUTurn();
            case ECHO2:
                return droneRadar.echo(currDir == DroneHeading.NORTH ? "N" : "S");
            case FLY2:
                if (groundDis == -1) {
                    current = State.SCAN;
                    groundDis = -2;
                }
                return droneController.fly();
            default:
                return null;
        }

    }

    public Phase getNextPhase() {
        logger.info("Second pass ended");
        return null;
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
                        current = State.U_TURN;
                    }
                }
            }
            if (extras.has("biomes")) {
                JSONArray biomes = extras.getJSONArray("biomes");
                if (biomes.length() == 1 && "OCEAN".equals(biomes.getString(0)) && !hasUturned) {
                    waitingForEcho = true;
                    current = State.ECHO2;
                }
            }
            if (waitingForEcho && extras.has("range")) {
                groundDis = extras.getInt("range");
                logger.info("Ground distance updated to: {}", groundDis);
                waitingForEcho = false;
                current = State.FLY2;
            }
        }
    }
}