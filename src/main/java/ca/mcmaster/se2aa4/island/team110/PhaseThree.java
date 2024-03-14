package ca.mcmaster.se2aa4.island.team110;

import ca.mcmaster.se2aa4.island.team110.Interfaces.Phase;

public class PhaseThree implements Phase{

  
  private enum State{
    ECHO, TURN, FLY, SCAN
  }

  private DroneController droneController = new DroneController();
  private DroneRadar droneRadar = new DroneRadar();
  private DroneScanner droneScanner = new DroneScanner();
  private State currentState;


  private boolean lastEchoFoundGround = true;
  

  public PhaseThree(){
    this.currentState = State.FLY;
  }

  public void updateLastEchoFoundGround(boolean found) {
    this.lastEchoFoundGround= found;
  }

  @Override
  public boolean reachedEnd() {
    return currentState == State.TURN;
  }

  @Override
  public String getNextDecision () {
    if (currentState== State.FLY) {
      currentState = State.SCAN; 
      return droneController.fly();
    }else if (currentState == State.SCAN) {
      currentState=State.ECHO; 
      return droneScanner.scan();
    }
    else if(currentState == State.ECHO ){
      if( lastEchoFoundGround){
        currentState= State.FLY;
        return droneRadar.echo("E");
      } else{
        currentState = State.TURN;
        return droneController.turn("LEFT");
      }
    } 
    else if (currentState == State.FLY){
      currentState = State.ECHO;
      lastEchoFoundGround = true;
      return droneController.fly();
    } else{
      return droneController.fly();
    }
      

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
