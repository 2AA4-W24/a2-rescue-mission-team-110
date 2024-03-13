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

    public DroneHeading turn(String turnHeading){
        switch(this) {
            case NORTH:
              return "E".equals(turnHeading) ? EAST : WEST;
            case EAST:
              return "N".equals(turnHeading) ? NORTH : SOUTH;
            case SOUTH:
              return "E".equals(turnHeading) ? EAST : WEST;
            case WEST:
              return "N".equals(turnHeading) ? NORTH : SOUTH;
            default:
              return this;
        }
    }
}