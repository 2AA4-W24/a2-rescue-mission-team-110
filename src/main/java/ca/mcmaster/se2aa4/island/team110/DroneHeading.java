package ca.mcmaster.se2aa4.island.team110;

public enum DroneHeading {
    NORTH("N"), 
    SOUTH("S"), 
    EAST("E"), 
    WEST("W");

    private String direction;

    private DroneHeading(String direction) {
      this.direction = direction;
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

    public void turn(String turnHeading){
        switch(this) {
            case NORTH:
                this.direction = turnHeading.equals("E") ? EAST.direction : WEST.direction;
                break;
            case EAST:
                this.direction = "N".equals(turnHeading) ? NORTH.direction : SOUTH.direction;
                break;
            case SOUTH:
                this.direction = "E".equals(turnHeading) ? EAST.direction : WEST.direction;
                break;
            case WEST:
                this.direction = "N".equals(turnHeading) ? NORTH.direction : SOUTH.direction;
                break;
        }
    }
}