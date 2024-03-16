package ca.mcmaster.se2aa4.island.team110;

import ca.mcmaster.se2aa4.island.team110.Interfaces.Phase;


public class PhaseThree implements Phase {


  private DroneController droneController = new DroneController();
  private DroneRadar droneRadar = new DroneRadar();
  private DroneScanner droneScanner = new DroneScanner();

  private State currentState = State.ECHO;
  private int turnStage = 0;
  private Direction currentDriection = Direction.S;
  

  private boolean isOutOfRange = false;
  public boolean checkEchoAfterTurn = false;
  


  private enum State{
    ECHO, FLY, SCAN, U_TURN
  }

  private enum Direction{
    N, S
  }


  @Override
  public boolean reachedEnd() {
    return isOutOfRange;
  }
  
  public void canUTurn(String response){
    if ("OUT_OF_RANGE".equals(response)){
      currentState = State.U_TURN;
    }
    else{
      currentState = State.FLY;
    }
  }

  private String makeUTurn(){
    switch(turnStage){
      case 0:
        turnStage++;
        return droneController.fly();
      case 1:
        turnStage++;
        return droneController.turn("E");
      case 2:
        turnStage++;
        return droneController.fly();
      case 3:
        turnStage++;
          if (currentDriection == Direction.S){
            currentDriection = Direction.N;
            return droneController.turn("N");
          } else if (currentDriection == Direction.N){
            currentDriection = Direction.S;
            currentState = State.ECHO;
            return droneController.turn("S");
          }
      case 4:
        turnStage = 0;
        checkEchoAfterTurn = true;
        currentState = State.FLY;
        return droneRadar.echo(currentDriection == Direction.N ? "N" : "S");
      default:
        return null;
    }
  }

  @Override
  public String getNextDecision () {
    if (checkEchoAfterTurn){
      checkEchoAfterTurn = false;
      currentState = State.ECHO;
      return droneRadar.echo(currentDriection == Direction.N ? "N" : "S");
    } else{
      switch(currentState){
        case ECHO:
          currentState = State.FLY;
          if (currentDriection == Direction.N){
            return droneRadar.echo("N");
          }else{
            return droneRadar.echo("S");
          }
        case FLY:
          currentState = State.SCAN;
          return droneController.fly();
        case SCAN:
          currentState = State.ECHO;
          return droneScanner.scan();
        case U_TURN:
          return makeUTurn();
        default:
          return null;
      }
    }
    
  }

  public void processEchoResultAfterUTurn(String response) {
    if ("OUT_OF_RANGE".equals(response)) {
        isOutOfRange = true;
        checkEchoAfterTurn = false;
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
