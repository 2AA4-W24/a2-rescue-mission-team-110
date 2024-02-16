package ca.mcmaster.se2aa4.island.team110;

import ca.mcmaster.se2aa4.island.team110.Interfaces.Radar;
import ca.mcmaster.se2aa4.island.team110.Enums.RadarResponse;
import ca.mcmaster.se2aa4.island.team110.Enums.Directions;

public class DroneRadar implements Radar {

  private Directions droneHeading;

  public DroneRadar(Directions droneHeading) {
    this.droneHeading = droneHeading;
  }

  @Override
  public RadarResponse getEchoResponse(Directions direction) {
    if (direction == getOppositeDirection(droneHeading)) {
      return RadarResponse.MIA; // Cannot send an echo in the opposite direction of the heading
    }

    // Placeholder: The logic for determining the radar response based on the map
    // information
    // This should be implemented when the map reading functionality is available
    int tilesToLand = processRadarInformation(direction);

    if (tilesToLand >= 0) {
      return RadarResponse.GROUND; // Ground detected within radar range
    } else {
      return RadarResponse.OUT_OF_RANGE; // No ground detected within radar range
    }
  }

  // This method needs to work with map
  private int processRadarInformation(Directions direction) {
    // LOGIC
    // Return -1 if land is not detected before the edge of the map.
    return -1;
  }

  private Directions getOppositeDirection(Directions heading) {
    switch (heading) {
      case NORTH:
        return Directions.SOUTH;
      case SOUTH:
        return Directions.NORTH;
      case EAST:
        return Directions.WEST;
      case WEST:
        return Directions.EAST;
      default:
        throw new IllegalArgumentException("Invalid drone heading");
    }
  }
}