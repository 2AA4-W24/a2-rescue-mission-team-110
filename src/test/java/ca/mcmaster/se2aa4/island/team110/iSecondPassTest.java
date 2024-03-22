package ca.mcmaster.se2aa4.island.team110;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.json.JSONObject;
import org.json.JSONArray;

import ca.mcmaster.se2aa4.island.team110.Phases.iSecondPass;
import ca.mcmaster.se2aa4.island.team110.Aerial.DroneHeading;

public class iSecondPassTest {
  private iSecondPass secondPass;
  private TestableRelativeMap map;

  @BeforeEach
  void setUp() {
    map = new TestableRelativeMap(DroneHeading.EAST); 
    secondPass = new iSecondPass(map);
  }

@Test
void testGroundDistanceUpdate() {
  JSONObject response = new JSONObject().put("extras", new JSONObject().put("range", 10));
  secondPass.updateState(response);
  String decisionJson = secondPass.getNextDecision();
  JSONObject decision = new JSONObject(decisionJson);
  assertEquals("fly", decision.getString("action"), "Action should be 'fly' when ground distance is updated");
}

  @Test
  void testInitialUTurn() {
    String decision = secondPass.getNextDecision();
    assertNotNull(decision, "Decision should not be null initially");
  }

  @Test
  void testEchoDeterminationNorth() {
    map.setCurrentHeading(DroneHeading.NORTH);
    secondPass.determineEcho();
    String decision = secondPass.getNextDecision();
    assertTrue(decision.contains("N") || decision.contains("W"), "Echo direction should be north or west when heading is north");
  }

  @Test
  void testEchoDeterminationEast() {
    map.setCurrentHeading(DroneHeading.EAST);
    secondPass.determineEcho();
    String decision = secondPass.getNextDecision();
    assertTrue(decision.contains("E"), "Echo direction should be east when heading is east");
  }

  @Test
  void testUpdateStateWithOutOfRange() {
    JSONObject response = new JSONObject().put("extras", new JSONObject().put("found", "OUT_OF_RANGE"));
    secondPass.updateState(response);
    assertTrue(secondPass.reachedEnd(), "Should have reached end when out of range");
  }

  @Test
  void testUpdateStateWithCreek() {
    JSONObject response = new JSONObject().put("extras", new JSONObject().put("creeks", new JSONArray().put("creek-id")));
    secondPass.updateState(response);
    assertTrue(map.hasCreekID("creek-id"), "Map should have creek-id after update");
  }

  @Test
  void testUpdateStateWithEmergencySite() {
    JSONObject response = new JSONObject().put("extras", new JSONObject().put("sites", new JSONArray().put("site-id")));
    secondPass.updateState(response);
    assertTrue(map.hasEmergencySite("site-id"), "Map should have site-id after update");
  }
}
