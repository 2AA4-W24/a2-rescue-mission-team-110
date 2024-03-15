package ca.mcmaster.se2aa4.island.team110;

import java.io.StringReader;

import org.apache.bcel.generic.INSTANCEOF;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.ace_design.island.bot.IExplorerRaid;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import ca.mcmaster.se2aa4.island.team110.Interfaces.Controller;
import ca.mcmaster.se2aa4.island.team110.Interfaces.Phase;
import ca.mcmaster.se2aa4.island.team110.Interfaces.Radar;
import ca.mcmaster.se2aa4.island.team110.Interfaces.Scanner;

public class Explorer implements IExplorerRaid {

    private final Logger logger = LogManager.getLogger();
    private Controller droneController;
    private Radar droneRadar;
    private Scanner droneScanner;
    private DroneHeading droneHeading;
    private RelativeMap relativeMap;
    private Phase currentPhase;

    private boolean hasCurrentTileScan = false;

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
        droneHeading = DroneHeading.getHeading(direction);
        relativeMap = new RelativeMap(droneHeading);
        Integer batteryLevel = info.getInt("budget");
        logger.info("The drone is facing {}", direction);
        logger.info("Battery level is {}", batteryLevel);
    }

    @Override
    public String takeDecision() {
        JSONObject decision = new JSONObject();
        
        if (currentPhase == null){
            currentPhase = new PhaseOne();
        }
        

        if(currentPhase.reachedEnd()){
            currentPhase = currentPhase.getNextPhase();
        }

        String decisionAction = currentPhase.getNextDecision();
        JSONObject actionDecision = new JSONObject(decisionAction);

        if (actionDecision.getString("action").equals("echo") || actionDecision.getString("action").equals("heading")){
            decision.put("action", actionDecision.get("action"));
            decision.put("parameters", actionDecision.getJSONObject("parameters"));
        } else{
            decision.put("action", actionDecision.get("action"));
        }
        
        
        logger.info("** Decision: {}", decision.toString());
        return decision.toString();
    }

    @Override
    public void acknowledgeResults(String s) {
        JSONObject response = new JSONObject(new JSONTokener(new StringReader(s)));
        logger.info("** Response received:\n"+response.toString(2));


        

        if(response.has("extras")) {
            JSONObject extras = response.getJSONObject("extras");
            if (extras != null && extras.has("found")) {
                if("GROUND".equals(extras.getString("found"))){
                    if(currentPhase instanceof PhaseOne){
                        ((PhaseOne) currentPhase).groundResponse(true);
                        if (((PhaseOne) currentPhase).reachedEnd()) {
                            currentPhase = currentPhase.getNextPhase();
                            
                        }
                    }
                } 
                
                else{
                    if (currentPhase instanceof PhaseOne) {
                        PhaseOne phaseOne = (PhaseOne) currentPhase;
                        if(phaseOne.reachedEnd()) {
                            currentPhase = currentPhase.getNextPhase();
                        } else {
                            phaseOne.setToFly();
                        }
                    }
                     else if (currentPhase instanceof PhaseThree){
                        PhaseThree phaseThree = (PhaseThree) currentPhase;
                        phaseThree.canUTurn(extras.getString("found"));

                        if ("OUT_OF_RANGE".equals(extras.getString("found")) && phaseThree.checkEchoAfterTurn){
                            phaseThree.processEchoResultAfterUTurn(extras.getString("echo"));
                        }

                    } 
                }
            }else if(extras.has("biomes")){
                JSONArray biomes = extras.getJSONArray("biomes");
                if (!(biomes.length()== 1 && "OCEAN".equals(biomes.getString(0)))){
                    if (currentPhase instanceof PhaseTwo) {
                        ((PhaseTwo) currentPhase).setHasScanGround(true);
                    }
                }
                
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
        return "no creek found";
    }

}