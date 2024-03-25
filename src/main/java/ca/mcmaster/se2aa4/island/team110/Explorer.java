package ca.mcmaster.se2aa4.island.team110;

import java.io.StringReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.ace_design.island.bot.IExplorerRaid;

import org.json.JSONObject;
import org.json.JSONTokener;

import ca.mcmaster.se2aa4.island.team110.Aerial.DroneHeading;
import ca.mcmaster.se2aa4.island.team110.Interfaces.Phase;
import ca.mcmaster.se2aa4.island.team110.Phases.FindGround;
import ca.mcmaster.se2aa4.island.team110.Records.Battery;
import ca.mcmaster.se2aa4.island.team110.DefaultJSONResponseParser;

public class Explorer implements IExplorerRaid {

    private final Logger logger = LogManager.getLogger();

    private DroneHeading droneHeading;
    private RelativeMap relativeMap;
    private Battery battery;
    private Phase current_phase;

    @Override
    public void initialize(String s) {
        logger.info("** Initializing the Exploration Command Center");
        JSONObject info = new JSONObject(new JSONTokener(new StringReader(s)));
        logger.info("** Initialization info:\n {}", info.toString(2));
        String direction = info.getString("heading");
        Integer batteryLevel = info.getInt("budget");

        //Initialize necessary modules
        droneHeading = DroneHeading.getHeading(direction);
        relativeMap = new RelativeMap(droneHeading);
        battery = new Battery(batteryLevel);
        DefaultJSONResponseParser parser = new DefaultJSONResponseParser();

        //Initialize current phase
        this.current_phase = new FindGround(relativeMap, battery, parser);

        logger.info("The drone is facing {}", direction);
        logger.info("Battery level is {}", batteryLevel);
    }

    @Override
    public String takeDecision() {
        JSONObject decision = new JSONObject();
        JSONObject nextAction = new JSONObject(current_phase.getNextDecision());

        decision.put("action", nextAction.get("action"));
        logger.info("** Decision: {}", decision.toString());

        if (nextAction.has("parameters")) {
            decision.put("parameters", nextAction.getJSONObject("parameters"));
        }
        return decision.toString();
    }

    @Override
    public void acknowledgeResults(String s) {
        JSONObject response = new JSONObject(new JSONTokener(new StringReader(s)));
        logger.info("** Response received:\n" + response.toString(2));

        this.current_phase.updateState(response);

        if (!this.current_phase.isFinal()) {
            if (this.current_phase.reachedEnd()) {
                this.current_phase = this.current_phase.getNextPhase();
            }
        }
        

        Integer cost = response.getInt("cost");
        logger.info("The cost of the action was {}", cost);
        String status = response.getString("status");
        logger.info("The status of the drone is {}", status);
        JSONObject extraInfo = response.getJSONObject("extras");
        logger.info("Additional information received: {}", extraInfo);
    }

    @Override
    public String deliverFinalReport() {
        String closestCreek = relativeMap.getClosestCreekId();
        logger.info("The closest creek to the emergency site is: {}", closestCreek);
        return closestCreek;
    }

}