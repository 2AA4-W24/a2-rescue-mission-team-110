package ca.mcmaster.se2aa4.island.team110;

public class jsonInfo {
  private final String heading;
  private final int budget;

  public jsonInfo(String heading, int budget){
    this.heading = heading;
    this.budget = budget;
    
  }

  public String getHeading() {
    return heading;
  }

  public int getBuget() {
    return budget;
  }

}
