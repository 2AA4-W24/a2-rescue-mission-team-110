package ca.mcmaster.se2aa4.island.team110;

import ca.mcmaster.se2aa4.island.team110.Interfaces.Phase;

public class PhaseTwo implements Phase {

  private DroneController droneController = new DroneController();
  private DroneScanner droneScanner = new DroneScanner();

  private int rangeToGround;
  private int flyCount = 0;

  private State currentState;

  public void setRangeToGround(int range){
    this.rangeToGround = range;
  }
  
  private enum State{
    FLY, SCAN
  }

  public PhaseTwo(){
    this.currentState = State.FLY;
  }

  @Override
  public boolean reachedEnd() {
    return flyCount == rangeToGround;
  }

  @Override
  public String getNextDecision () {
    if (flyCount< rangeToGround){
      flyCount++;
      if(currentState == State.FLY){
        currentState = State.SCAN;
        return droneController.fly();
      } else{
        currentState = State.FLY;
        return droneScanner.scan();
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
