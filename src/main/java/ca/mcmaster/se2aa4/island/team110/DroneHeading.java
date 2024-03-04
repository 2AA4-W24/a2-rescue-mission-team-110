package ca.mcmaster.se2aa4.island.team110;

public class DroneHeading {
  
  private enum Heading{
    NORTH, SOUTH, EAST, WEST;
  }

  private Heading currDirection;

  public DroneHeading(String initialDirection){
    this.currDirection = parseCurrentDirection(initialDirection);
  }

  public void turn(String turnHeading){

    switch(this.currDirection) {

      case NORTH:
        this.currDirection = "E".equals(turnHeading) ? Heading.EAST : Heading.WEST;
        break;

      case EAST:
        this.currDirection = "N".equals(turnHeading) ? Heading.NORTH : Heading.SOUTH;
        break;

      case SOUTH:
        this.currDirection = "E".equals(turnHeading) ? Heading.EAST : Heading.WEST;
        break;

      case WEST:
        this.currDirection = "N".equals(turnHeading) ? Heading.NORTH : Heading.SOUTH;
    }
  }

  
  private Heading parseCurrentDirection(String direction) {
    return Heading.valueOf(direction);
  }

  public String getCurrentDirection(){
    return currDirection.name();

  }

}
