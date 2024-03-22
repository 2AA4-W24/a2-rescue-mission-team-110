package ca.mcmaster.se2aa4.island.team110;


import ca.mcmaster.se2aa4.island.team110.Aerial.DroneHeading;

import ca.mcmaster.se2aa4.island.team110.Aerial.DroneHeading;
import java.util.ArrayList;
import java.util.List;


public class TestableRelativeMap extends RelativeMap {

  private List<String> creekIDs = new ArrayList<>();
  private boolean emergencySiteFound = false;

  public TestableRelativeMap(DroneHeading initialHeading) {
    super(initialHeading);
  }

  
  public void setCurrentHeading(DroneHeading newHeading) {
    this.current_heading = newHeading;
  }

  public void addCreekID(String creekID) {
    creekIDs.add(creekID);
  }

  public String getCreekID() {
    return creekIDs.isEmpty() ? null : creekIDs.get(0);
  }

  public void setEmergencySiteFound(boolean found) {
    this.emergencySiteFound = found;
  }

  public boolean hasEmergencySite() {
    return this.emergencySiteFound;
}

  
}
