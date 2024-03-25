package ca.mcmaster.se2aa4.island.team110;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import ca.mcmaster.se2aa4.island.team110.Phases.iFirstPass;
import ca.mcmaster.se2aa4.island.team110.Aerial.DroneHeading;
import ca.mcmaster.se2aa4.island.team110.Records.Battery;


public class iFirstPassTest {
  private iFirstPass firstPass;
  private TestableRelativeMap map;
  private Battery battery;
  private DefaultJSONResponseParser parser;

  @BeforeEach
  void setUp() {
      map = new TestableRelativeMap(DroneHeading.EAST);
      battery = new Battery(1000); 
      parser = new DefaultJSONResponseParser();
      firstPass = new iFirstPass(map, battery, parser);
  }

  @Test
  void testInitialState() {
    String decision = firstPass.getNextDecision();
    String expectedDecision = "scan";
    String actualDecision = new JSONObject(decision).getString("action");
    assertEquals(expectedDecision, actualDecision);
  }

  @Test
  void testScanToEchoTransition() {
    firstPass.getNextDecision();
    firstPass.updateState(new JSONObject().put("extras", new JSONObject()));
    String decision = firstPass.getNextDecision();
    String expectedDecision = "echo";
    String actualDecision = new JSONObject(decision).getString("action");
    assertEquals(expectedDecision, actualDecision);
  }

  @Test
  void testEchoToFlyTransition() {
    firstPass.getNextDecision();
    firstPass.updateState(new JSONObject().put("extras", new JSONObject()));
    firstPass.getNextDecision();
    firstPass.updateState(new JSONObject().put("extras", new JSONObject().put("range", 2)));
    String decision = firstPass.getNextDecision();
    String expectedDecision = "fly";
    String actualDecision = new JSONObject(decision).getString("action");
    assertEquals(expectedDecision, actualDecision);
  }
 
  @Test
  void testUpdateStateWithCreeks() {
    JSONObject response = new JSONObject()
        .put("extras", new JSONObject().put("creeks", new JSONArray().put("creek-id")));
    firstPass.updateState(response);
    assertNotNull(map.getCreekID());
  }

  @Test
  void testGoHomeWhenBatteryLow() {
    battery.updateBatteryLevel(800); 
    firstPass.updateState(new JSONObject()); 
    assertTrue(firstPass.reachedEnd(), "Phase should signal to go home due to low battery.");
  }

  @Test
  void testFlyTowardsGroundWhenRangeDetected() {
    JSONObject echoResponse = new JSONObject().put("extras", new JSONObject().put("range", 3));
    firstPass.updateState(echoResponse); 
    firstPass.getNextDecision(); 
    assertEquals("fly", new JSONObject(firstPass.getNextDecision()).getString("action"), "Drone should fly towards ground when range is detected.");
  }

  @Test
  void testSuccessfulUTurnUpdatesStateCorrectly() {
    firstPass.updateState(new JSONObject().put("extras", new JSONObject().put("found", "OUT_OF_RANGE")));
    for (int i = 0; i < 5; i++) {
        firstPass.getNextDecision(); 
    }
    assertFalse(firstPass.reachedEnd(), "Phase should not end immediately after a U-turn.");
  }

  @Test
  void testHandleOutOfRangeAfterUTurn() {
    firstPass.updateState(new JSONObject().put("extras", new JSONObject().put("found", "OUT_OF_RANGE")));
    for (int i = 0; i < 5; i++) {
        firstPass.getNextDecision();
    }
    firstPass.updateState(new JSONObject().put("extras", new JSONObject().put("found", "OUT_OF_RANGE")));
    assertTrue(firstPass.reachedEnd());
  }

  @Test
  void testBatteryDepletionBeforeUTurnCompletion() {
    battery.updateBatteryLevel(950);
    firstPass.updateState(new JSONObject().put("extras", new JSONObject().put("found", "OUT_OF_RANGE")));
    for (int i = 0; i < 3; i++) {
        firstPass.getNextDecision();
    }
    assertTrue(firstPass.reachedEnd());
  }

  @Test
  void testGroundFoundDuringUTurn() {
    firstPass.updateState(new JSONObject().put("extras", new JSONObject().put("found", "OUT_OF_RANGE")));
    for (int i = 0; i < 2; i++) {
        firstPass.getNextDecision();
    }
    firstPass.updateState(new JSONObject().put("extras", new JSONObject().put("range", 1)));
    firstPass.getNextDecision();
    assertFalse(firstPass.reachedEnd());
  }

}
