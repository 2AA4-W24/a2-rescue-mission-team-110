package ca.mcmaster.se2aa4.island.team110;

import java.io.StringReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.ace_design.island.bot.IExplorerRaid;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import ca.mcmaster.se2aa4.island.team110.Interfaces.Controller;
import ca.mcmaster.se2aa4.island.team110.Interfaces.Radar;
import ca.mcmaster.se2aa4.island.team110.Interfaces.Scanner;

public class Explorer implements IExplorerRaid {

    private final Logger logger = LogManager.getLogger();
    private Controller droneController;
    private Radar droneRadar;
    private Scanner droneScanner;

    private boolean hasCurrentTileScan = false;
    private boolean flyOrEcho = false; //False means Echo, True means Fly
    private boolean turn = false; //False means don't turn, True means turn (will turn True when scan finds ground)
    private boolean foundGround = false;
    private int rangeToGround = 0;
    private int flyCount = 0;
    private boolean overGround = false;
    private int count = 0;


    public Explorer() {
        this.droneController = new DroneController();
        this.droneRadar = new DroneRadar();
        this.droneScanner = new DroneScanner();
    }

    @Override
    public void initialize(String s) {
        logger.info("** Initializing the Exploration Command Center");
        JSONObject info = new JSONObject(new JSONTokener(new StringReader(s)));
        logger.info("** Initialization info:\n {}",info.toString(2));
        String direction = info.getString("heading");
        Integer batteryLevel = info.getInt("budget");
        logger.info("The drone is facing {}", direction);
        logger.info("Battery level is {}", batteryLevel);
    }

    @Override
    public String takeDecision() {
        JSONObject decision = new JSONObject();
        // if (overGround) {
        //     decision.put("action", "stop");
        // }
        // else if(!hasCurrentTileScan){
        //     decision = new JSONObject(droneScanner.scan());
        //     hasCurrentTileScan = true;
        // }
        // else if (foundGround && flyCount != rangeToGround) {
        //     if (turn) {
        //         decision.put("action", "heading");
        //         decision.put("parameters", new JSONObject().put("directions", "S"));
        //         turn = false;
        //         flyCount++;
        //     }
        //     else {
        //         decision.put("action", "fly");
        //         flyCount++;
        //         hasCurrentTileScan = false;
        //     }

        //     if (flyCount == rangeToGround) {
        //         overGround = true;
        //     }

            

        // }
        // else {
        //     if (!flyOrEcho && !foundGround) {
        //         decision.put("action", "fly");
        //         //decision.put("parameters", new JSONObject().put("directions", "S"));
        //         flyOrEcho = true;
        //     }
        //     else if (flyOrEcho) {
        //         decision.put("action", "fly");
        //         flyOrEcho = false;
        //         hasCurrentTileScan = false;
        //     } //this is palceholder for now

        //}

        if (count == 0) {
            decision.put("action", "scan");
            count++;
        }

        else if (count == 1) {
            decision.put("action", "fly");
            count++;
        }

        else if (count == 2) {
            decision.put("action", "scan");
            count++;
        }

        else {
            decision.put("action", "stop");
        }



        
        logger.info("** Decision: {}", decision.toString());
        return decision.toString();
    }

    @Override
    public void acknowledgeResults(String s) {
        JSONObject response = new JSONObject(new JSONTokener(new StringReader(s)));
        logger.info("** Response received:\n"+response.toString(2));

        

        
        // if(response.has("extras")) {
        //     JSONObject extras = response.getJSONObject("extras");
        //     if (extras != null && extras.has("found")) {
        //         String found = extras.getString("found");
        //         if (found.equals("GROUND")) {
        //             turn = true;
        //             foundGround = true;
        //             rangeToGround = extras.getInt("range");
        //         }
        //         else {
        //             turn = false;
        //         }
        //     }
        // }
        

        

        Integer cost = response.getInt("cost");
        logger.info("The cost of the action was {}", cost);

    
        // if(action.equals("scan")){
        //     hasCurrentTileScan  = true;
       

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