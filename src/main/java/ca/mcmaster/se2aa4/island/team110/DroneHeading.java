package ca.mcmaster.se2aa4.island.team110;

public enum DroneHeading {
    NORTH("N"), 
    SOUTH("S"), 
    EAST("E"), 
    WEST("W");

   
    private DroneHeading(String direction) {

    }

    public static DroneHeading getHeading(String direction) {
      switch(direction) {
        case "N":
          return NORTH;
        case "S":
          return SOUTH;
        case "E":
          return EAST;
        case "W":
          return WEST;
        default:  
          return null;
      }
    }

    public DroneHeading turn(String turnHeading){
        switch(turnHeading) {
          case "LEFT":
            return turnLeft();
          case "RIGHT":
            return turnRight();
          default:
            return this; 
        }
    }

    private DroneHeading turnLeft() {
        switch(this) {
          case NORTH:
            return WEST;
          case SOUTH:
            return EAST;
          case EAST:
            return NORTH;
          case WEST:
            return SOUTH;
          default:
            return this;
        }
    }

    private DroneHeading turnRight() {
        switch(this) {
          case NORTH:
            return EAST;
          case SOUTH:
            return WEST;
          case EAST:
            return SOUTH;
          case WEST:
            return NORTH;
          default:
            return this;
        }
    }
}