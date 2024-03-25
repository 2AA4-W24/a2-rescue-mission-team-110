package ca.mcmaster.se2aa4.island.team110;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.json.JSONObject;
import ca.mcmaster.se2aa4.island.team110.Phases.EmergencySite;
import ca.mcmaster.se2aa4.island.team110.Aerial.DroneHeading;
import ca.mcmaster.se2aa4.island.team110.Records.Point;

public class EmergencySiteTest {
  private EmergencySite emergencySite;
  private TestableRelativeMap map;

  @BeforeEach
  void setUp() {
      map = new TestableRelativeMap(DroneHeading.NORTH);
      emergencySite = new EmergencySite(map);
  }

  @Test
  void testStopPhase() {
      map.setCurrentPosition(new Point(0, 0));
      map.setEmergencySiteLocation(new Point(1, 0));
      emergencySite.updateState(new JSONObject());
      assertFalse(emergencySite.reachedEnd());
  }

  @Test
  void testReachedEmergencySite() {
      map.setCurrentPosition(new Point(0, 0));
      map.setEmergencySiteLocation(new Point(0, 0));
      emergencySite.updateState(new JSONObject());
      emergencySite.getNextDecision();
      assertTrue(emergencySite.reachedEnd());
  }
}
