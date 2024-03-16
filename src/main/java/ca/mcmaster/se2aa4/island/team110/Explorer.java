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

public class Explorer implements IExplorerRaid {

    private final Logger logger = LogManager.getLogger();

    private DroneHeading droneHeading;
    private RelativeMap relativeMap;
    private Phase current;

    public Explorer() {
        this.current = new FindGround();
    }

    @Override
    public void initialize(String s) {
        logger.info("** Initializing the Exploration Command Center");
        JSONObject info = new JSONObject(new JSONTokener(new StringReader(s)));
        logger.info("** Initialization info:\n {}", info.toString(2));
        String direction = info.getString("heading");
        droneHeading = DroneHeading.getHeading(direction);
        relativeMap = new RelativeMap(droneHeading);
        Integer batteryLevel = info.getInt("budget");
        logger.info("The drone is facing {}", direction);
        logger.info("Battery level is {}", batteryLevel);
    }

    @Override
    public String takeDecision() {
        JSONObject decision = new JSONObject();

        if (current.reachedEnd()) {
            current = current.getNextPhase();
        }

        String decisionAction = current.getNextDecision();
        JSONObject actionDecision = new JSONObject(decisionAction);

        if (actionDecision.getString("action").equals("echo") || actionDecision.getString("action").equals("heading")) {
            decision.put("action", actionDecision.get("action"));
            decision.put("parameters", actionDecision.getJSONObject("parameters"));
        } else {
            decision.put("action", actionDecision.get("action"));
        }

        logger.info("** Decision: {}", decision.toString());
        return decision.toString();
    }

    @Override
    public void acknowledgeResults(String s) {
        JSONObject response = new JSONObject(new JSONTokener(new StringReader(s)));
        logger.info("** Response received:\n" + response.toString(2));

        current.updateState(response);

        if (current.reachedEnd()) {
            current = current.getNextPhase();
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
        return "no creek found";
    }

}