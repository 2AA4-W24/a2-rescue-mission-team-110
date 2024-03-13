package ca.mcmaster.se2aa4.island.team110;

import org.json.JSONObject;

import ca.mcmaster.se2aa4.island.team110.Interfaces.Phase;

public class PhaseOne implements Phase{

  
  private enum State{
    ECHO, TURN, FLY
  }

  private DroneController droneController = new DroneController();
  private DroneRadar droneRadar = new DroneRadar();
  private State currentState;


  private boolean groundDetected = false;
  

  public PhaseOne(){
    this.currentState = State.ECHO;
  }

  @Override
  public boolean reachedEnd() {
    return groundDetected && currentState == State.FLY;
  }

  @Override
  public String getNextDecision () {
    if(currentState == State.ECHO ){
      currentState = State.FLY;
      return droneRadar.echo("S");
    } 
    else if (currentState == State.TURN){
      currentState = State.FLY;
      return droneController.turn("RIGHT");
    } 
    else {
      return droneController.fly();
    }
      

  }

  public void groundResponse(boolean groundFound) {
    groundDetected = groundFound;
    if (groundDetected){
      currentState = State.TURN;
    } else{
      currentState = State.FLY;
    }
  }

  @Override
  public Phase getNextPhase() {
    return new PhaseTwo();
  }

  @Override
  public boolean isFinal() {
    return false;
  }
  
}
