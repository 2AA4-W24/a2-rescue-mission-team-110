package ca.mcmaster.se2aa4.island.team110.Phases;

import org.json.JSONObject;


import ca.mcmaster.se2aa4.island.team110.Aerial.DroneController;
import ca.mcmaster.se2aa4.island.team110.Aerial.DroneRadar;
import ca.mcmaster.se2aa4.island.team110.Aerial.DroneHeading;

import ca.mcmaster.se2aa4.island.team110.RelativeMap;
import ca.mcmaster.se2aa4.island.team110.Records.Battery;

import ca.mcmaster.se2aa4.island.team110.Interfaces.Phase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MoveToGround implements Phase {
    private final Logger logger = LogManager.getLogger();

    private DroneController droneController = new DroneController();
    private DroneRadar droneRadar = new DroneRadar();

    private RelativeMap map;
    private Battery battery;

    private State current_state = State.ECHO;
    private int range = -1;
    private String echo_direction;

    private boolean reachedGround = false;

    private boolean goHome = false;

    public MoveToGround(RelativeMap map, Battery battery) {
        this.map = map;
        this.battery = battery;
    }

    private enum State {
        FLY, ECHO, GO_HOME;
    }

    public void determineEcho() {
        if (map.getCurrentHeading() == DroneHeading.NORTH || map.getCurrentHeading() == DroneHeading.SOUTH) {
            this.echo_direction = map.getCurrentHeading() == DroneHeading.NORTH ? "N" : "S";
        } else if (map.getCurrentHeading() == DroneHeading.EAST || map.getCurrentHeading() == DroneHeading.WEST) {
            this.echo_direction = map.getCurrentHeading() == DroneHeading.EAST ? "E" : "W";
        }
    }

    @Override
    public boolean reachedEnd() {
        return reachedGround;
    }

    @Override
    public String getNextDecision() {
        logger.info("Phase: MoveToGround");
        if (current_state == State.FLY && range > 1) {
            current_state = State.FLY;
            range--;
        } else if (range == 1){
            reachedGround = true;
        }
        switch (current_state) {
            case ECHO:
                determineEcho();
                current_state = State.FLY;
                return droneRadar.echo(this.echo_direction);
            case FLY:
                return droneController.fly();
            default:
                return null;
        }
    }

    @Override
    public Phase getNextPhase() {
        return new iFirstPass(this.map, this.battery);
    }

    @Override
    public void updateState(JSONObject response) {
        if (response.has("extras")) {
            JSONObject extras = response.getJSONObject("extras");
            if (extras.has("range")) {
                range = extras.getInt("range");
                logger.info("Range updated to: {}", range);
            }
        }
        if (this.goHome = true) {
            this.current_state = State.GO_HOME;
        }
    }

    @Override
    public boolean isFinal() {
        return false;
    }
}