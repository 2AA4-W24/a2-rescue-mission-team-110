package ca.mcmaster.se2aa4.island.team110;


import ca.mcmaster.se2aa4.island.team110.Aerial.DroneHeading;

public class TestableRelativeMap extends RelativeMap {
  public TestableRelativeMap(DroneHeading initialHeading) {
    super(initialHeading);
  }

  
  public void setCurrentHeading(DroneHeading newHeading) {
    this.current_heading = newHeading;
  }
}
