package ca.mcmaster.se2aa4.island.team110;

import org.json.JSONObject;

import ca.mcmaster.se2aa4.island.team110.Interfaces.Phase;

public class PhaseTwo implements Phase {

  private DroneController droneController = new DroneController();
  private DroneScanner droneScanner = new DroneScanner();
  private State currentState;

  private boolean hasScanGround = false;
  

  private enum State{
    FLY, SCAN
  }

  public void setHasScanGround(boolean hasScanGround) {
    this.hasScanGround= hasScanGround;
  }

  
  public PhaseTwo(){
    this.currentState = State.SCAN;
  }

  @Override
  public boolean reachedEnd() {
    return hasScanGround;
  }

  @Override
  public String getNextDecision () {
    if (!hasScanGround){
      if (currentState == State.SCAN){
        currentState = State.FLY;
        return droneScanner.scan();
      }else{
        currentState = State.SCAN;
        return droneController.fly();
      }
      
    }
    return null;
    
    
  }

  @Override
  public Phase getNextPhase() {
    return new PhaseThree();
  }

  @Override
  public boolean isFinal() {
    return false;
  }
}
