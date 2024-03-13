package ca.mcmaster.se2aa4.island.team110;

import org.json.JSONObject;

import ca.mcmaster.se2aa4.island.team110.Interfaces.Phase;

public class PhaseTwo implements Phase {

  private DroneController droneController = new DroneController();

  private int rangeToGround;
  private int flyCount = 0;

  public void setRangeToGround(int range){
    this.rangeToGround = range;
  }
  

  @Override
  public boolean reachedEnd() {
    return flyCount == rangeToGround;
  }

  @Override
  public String getNextDecision () {
    if (flyCount< rangeToGround){
      flyCount++;
      return droneController.fly();
    }
    return null;
    
    
  }

  @Override
  public Phase getNextPhase() {
    return null;
  }

  @Override
  public boolean isFinal() {
    return true;
  }
}
