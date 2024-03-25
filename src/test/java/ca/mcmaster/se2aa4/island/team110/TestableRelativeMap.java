package ca.mcmaster.se2aa4.island.team110;


import ca.mcmaster.se2aa4.island.team110.Aerial.DroneHeading;
import ca.mcmaster.se2aa4.island.team110.Records.Point;

import java.util.ArrayList;
import java.util.List;


public class TestableRelativeMap extends RelativeMap {

  private List<String> creekIDs = new ArrayList<>();
  private boolean emergencySiteFound = false;
  private String emergencySiteID;
  private Point currentPosition;
  private Point emergencySiteLocation;
  

  public TestableRelativeMap(DroneHeading initialHeading) {
    super(initialHeading);
  }

  public void setEmergencySiteLocation(Point emergencySiteLocation) {
    this.emergencySiteLocation = emergencySiteLocation;
  }

  
  public void setCurrentHeading(DroneHeading newHeading) {
    this.current_heading = newHeading;
  }

  public void addCreekID(String creekID) {
    creekIDs.add(creekID);
  }

  public boolean hasCreekID(String creekID) {
    return creekIDs.contains(creekID);
  }

  public String getCreekID() {
    return creekIDs.isEmpty() ? null : creekIDs.get(0);
  }

  public void setEmergencySiteFound(boolean found) {
    this.emergencySiteFound = found;
  }

  public void addEmergencySiteID(String siteID) {
    this.emergencySiteID = siteID;
    setEmergencySiteFound(true);
  }

  public boolean hasEmergencySite(String siteID) {
    return emergencySiteFound && emergencySiteID.equals(siteID);
  }

  public void setCurrentPosition(Point newPosition) {
    this.currentPosition = newPosition;
  }

  @Override
  public Point getCurrentPosition() {
      return this.currentPosition;
  }

  @Override
  public Point getEmergencySiteLocation() {
    return this.emergencySiteLocation;
  }

  

  
}
