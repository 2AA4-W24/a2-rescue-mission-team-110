package ca.mcmaster.se2aa4.island.team110;

import ca.mcmaster.se2aa4.island.team110.Aerial.DroneHeading;
import ca.mcmaster.se2aa4.island.team110.Phases.MoveToGround;
import ca.mcmaster.se2aa4.island.team110.Records.Battery;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MoveToGroundTest {
    private MoveToGround moveToGround;
    private TestableRelativeMap map;
    private Battery battery;
    private DefaultJSONResponseParser parser;

    @BeforeEach
    void setUp() {
      map = new TestableRelativeMap(DroneHeading.EAST);
      battery = new Battery(1000); 
      parser = new DefaultJSONResponseParser();
      moveToGround = new MoveToGround(map, battery, parser);
    }

    @Test
    void testInitialEchoDirectionBasedOnHeading() {
      String decision = moveToGround.getNextDecision();
      JSONObject decisionObj = new JSONObject(decision);
      assertTrue(decisionObj.getString("action").equals("echo"));
      assertTrue(decisionObj.getJSONObject("parameters").getString("direction").equals("E"));
    }

    @Test
    void testUpdateStateWithRangeAndTransitionToFlying() {
      JSONObject echoResponse = new JSONObject().put("extras", new JSONObject().put("range", 3));
      moveToGround.updateState(echoResponse);
      String decision = moveToGround.getNextDecision(); 
      String expectedAction = "fly";
      String actualAction = new JSONObject(decision).getString("action");
      assertEquals(expectedAction, actualAction);
    }

    @Test
    void testFlyUntilRangeIsOne() {
      JSONObject echoResponse = new JSONObject().put("extras", new JSONObject().put("range", 1));
      moveToGround.updateState(echoResponse);
      moveToGround.getNextDecision(); 
      boolean reachedEnd = moveToGround.reachedEnd();
      assertTrue(reachedEnd); 
    }

    @Test
    void testReachedGroundEndsPhase() {
      JSONObject echoResponse = new JSONObject().put("extras", new JSONObject().put("range", 1));
      moveToGround.updateState(echoResponse);
      moveToGround.getNextDecision(); 
      boolean reachedEnd = moveToGround.reachedEnd();
      assertTrue(reachedEnd);
    }

    

    @Test
    void testEchoNORTH() {
      map.setCurrentHeading(DroneHeading.NORTH);
      String decision = moveToGround.getNextDecision();
      String direction = new JSONObject(decision).getJSONObject("parameters").getString("direction");
      assertEquals("N", direction);
    }

    @Test
    void testEchoSOUTH() {
      map.setCurrentHeading(DroneHeading.SOUTH);
      String decision = moveToGround.getNextDecision();
      String direction = new JSONObject(decision).getJSONObject("parameters").getString("direction");
      assertEquals("S", direction);
    }

    @Test
    void testEchoEAST() {
      map.setCurrentHeading(DroneHeading.EAST);
      String decision = moveToGround.getNextDecision();
      String direction = new JSONObject(decision).getJSONObject("parameters").getString("direction");
      assertEquals("E", direction);
    }

    @Test
    void testEchoWEST() {
      map.setCurrentHeading(DroneHeading.WEST);
      String decision = moveToGround.getNextDecision();
      String direction = new JSONObject(decision).getJSONObject("parameters").getString("direction");
      assertEquals("W", direction);
    }




    @Test
    void testInvalidRangeDoesNotEndPhase() {
      JSONObject echoResponse = new JSONObject().put("extras", new JSONObject().put("range", -1));
      moveToGround.updateState(echoResponse);
      assertFalse(moveToGround.reachedEnd());
    }

    @Test
    void testBehaviorReflectsInternalState() {
      moveToGround.updateState(new JSONObject().put("extras", new JSONObject().put("range", 3)));
      String firstAction = moveToGround.getNextDecision();
      assertTrue(new JSONObject(firstAction).getString("action").equals("fly"));
      String secondAction = moveToGround.getNextDecision();
      assertTrue(new JSONObject(secondAction).getString("action").equals("fly"));
      moveToGround.updateState(new JSONObject().put("extras", new JSONObject().put("range", 1)));
      moveToGround.getNextDecision();
      assertTrue(moveToGround.reachedEnd());
    }

    @Test
    void testGoHomeWhenBatteryLow() {
      battery.updateBatteryLevel(800); 
      moveToGround.updateState(new JSONObject());
      assertTrue(moveToGround.reachedEnd());
    }

    @Test
    void testCorrectActionOnEdgeOfGround() {
      moveToGround.updateState(new JSONObject().put("extras", new JSONObject().put("range", 2)));
      String decision = moveToGround.getNextDecision();
      assertTrue(new JSONObject(decision).getString("action").equals("fly"), "Should fly towards ground when range is more than 1");
      
      moveToGround.updateState(new JSONObject().put("extras", new JSONObject().put("range", 1)));
      assertTrue(moveToGround.reachedEnd(), "Should have reached ground and end phase");
    }

    @Test
    void testBehaviorAfterReachingGround() {
      moveToGround.updateState(new JSONObject().put("extras", new JSONObject().put("range", 1)));
      moveToGround.getNextDecision(); 
      assertTrue(moveToGround.reachedEnd(), "Phase should end after reaching ground");
    }
}

