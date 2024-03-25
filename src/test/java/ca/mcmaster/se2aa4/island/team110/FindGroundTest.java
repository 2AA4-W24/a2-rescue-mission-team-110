package ca.mcmaster.se2aa4.island.team110;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import ca.mcmaster.se2aa4.island.team110.Phases.FindGround;
import ca.mcmaster.se2aa4.island.team110.Aerial.DroneHeading;
import ca.mcmaster.se2aa4.island.team110.Records.Battery;
import ca.mcmaster.se2aa4.island.team110.DefaultJSONResponseParser;

public class FindGroundTest {
    private FindGround findGround;
    private TestableRelativeMap map;
    private Battery battery;
    private DefaultJSONResponseParser parser;

    @BeforeEach
    void setUp() {
        map = new TestableRelativeMap(DroneHeading.EAST);
        battery = new Battery(1000); 
        parser = new DefaultJSONResponseParser();
        findGround = new FindGround(map, battery, parser);
    }

    @Test
    void testInitialFindGroundState() {
        String decision = findGround.getNextDecision();
        assertTrue(new JSONObject(decision).getString("action").equals("echo"));
    }

    @Test
    void testGroundDetectionSwitchesState() {
        JSONObject jsonResponse = new JSONObject().put("extras", new JSONObject().put("found", "GROUND"));
        findGround.updateState(jsonResponse);
        String decision = findGround.getNextDecision();
        assertEquals("heading", new JSONObject(decision).getString("action"));
    }

    @Test
    void testFlyAfterGroundDetection() {
        findGround.updateState(new JSONObject().put("found", true));
        findGround.getNextDecision(); 
        String decision = findGround.getNextDecision();
        assertEquals("fly", new JSONObject(decision).getString("action"));
    }


    @Test
    void testUpdateStateWithNoGroundFound() {
        JSONObject jsonResponse = new JSONObject().put("extras", new JSONObject().put("found", "OUT_OF_RANGE"));
        findGround.updateState(jsonResponse);
        String decision = findGround.getNextDecision();
        assertEquals("fly", new JSONObject(decision).getString("action"));
    }

    @Test
    void testFlyStateAfterEchoWithNoGround() {
        JSONObject jsonResponse = new JSONObject().put("extras", new JSONObject().put("found", "OUT_OF_RANGE"));
        findGround.updateState(jsonResponse);
        String decision = findGround.getNextDecision();
        assertEquals("fly", new JSONObject(decision).getString("action"));
    }

    @Test
    void testTurnCompletedEndsPhase() {
        JSONObject jsonResponse = new JSONObject().put("extras", new JSONObject().put("found", "GROUND"));
        findGround.updateState(jsonResponse);
        findGround.getNextDecision();
        findGround.getNextDecision();
        assertTrue(findGround.reachedEnd());
    }

    @Test
    void testOutOfMapEchoResponse() {
        JSONObject echoResponse = new JSONObject().put("cost", 1).put("extras", new JSONObject().put("range", 0).put("found", "OUT_OF_RANGE")).put("status", "OK");
        findGround.updateState(echoResponse);
        String decision = findGround.getNextDecision();
        assertTrue(decision.contains("fly") || decision.contains("turn"));
    }

    @Test
    void testBatteryThresholdForReturn() {
        battery.updateBatteryLevel(800); 
        findGround.updateState(new JSONObject());
        String decision = findGround.getNextDecision();
        assertEquals("fly", new JSONObject(decision).getString("action"), "Drone should return due to low battery.");
    }

    @Test
    void testInvalidEchoResponseHandling() {
        JSONObject invalidResponse = new JSONObject().put("status", "ERROR");
        findGround.updateState(invalidResponse);
        String decision = findGround.getNextDecision();
        assertNotNull(decision, "Decision should not be null even with invalid response.");
    }

    @Test
    void testCriticalBatteryLevelPreventsFurtherAction() {
        battery.updateBatteryLevel(950); 
        findGround.updateState(new JSONObject());
        String decision = findGround.getNextDecision();
        assertEquals("fly", new JSONObject(decision).getString("action"), "Drone should prioritize returning home on critical battery.");
    }
}
