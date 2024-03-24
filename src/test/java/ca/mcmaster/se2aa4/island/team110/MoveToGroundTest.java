// package ca.mcmaster.se2aa4.island.team110;

// import ca.mcmaster.se2aa4.island.team110.Aerial.DroneHeading;
// import ca.mcmaster.se2aa4.island.team110.TestableRelativeMap;
// import ca.mcmaster.se2aa4.island.team110.Phases.MoveToGround;
// import org.json.JSONObject;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import static org.junit.jupiter.api.Assertions.*;

// public class MoveToGroundTest {
//   private MoveToGround moveToGround;
//   private TestableRelativeMap map;

//   @BeforeEach
//   void setUp() {
//     map = new TestableRelativeMap(DroneHeading.EAST);
//     moveToGround = new MoveToGround(map);
//   }

//   @Test
//   void testInitialEchoDirectionBasedOnHeading() {
//     moveToGround.determineEcho();
//     String decision = moveToGround.getNextDecision();
//     assertTrue(new JSONObject(decision).getString("action").equals("echo"));
//     assertTrue(new JSONObject(decision).getJSONObject("parameters").getString("direction").equals("E"));
//   }

//   @Test
//   void testUpdateStateWithRangeAndTransitionToFlying() {
//       JSONObject echoResponse = new JSONObject().put("extras", new JSONObject().put("range", 3));
//       moveToGround.updateState(echoResponse);
//       String decision = moveToGround.getNextDecision(); 
//       assertEquals("echo", new JSONObject(decision).getString("action"));
//   }

//   @Test
//   void testFlyUntilRangeIsOne() {
//     JSONObject echoResponse = new JSONObject().put("extras", new JSONObject().put("range", 1));
//     moveToGround.updateState(echoResponse);
//     moveToGround.getNextDecision(); 
//     assertTrue(moveToGround.reachedEnd()); 
//   }

//   @Test
//   void testReachedGroundEndsPhase() {
//     JSONObject echoResponse = new JSONObject().put("extras", new JSONObject().put("range", 1));
//     moveToGround.updateState(echoResponse);
//     moveToGround.getNextDecision(); 
//     assertTrue(moveToGround.reachedEnd());
//   }

  

//   @Test
//   void testEchoNORTH() {
//       map.setCurrentHeading(DroneHeading.NORTH);
//       String decision = moveToGround.getNextDecision();
//       String direction = new JSONObject(decision).getJSONObject("parameters").getString("direction");
//       assertEquals("N", direction);
//   }

//   @Test
//   void testEchoSOUTH() {
//       map.setCurrentHeading(DroneHeading.SOUTH);
//       String decision = moveToGround.getNextDecision();
//       String direction = new JSONObject(decision).getJSONObject("parameters").getString("direction");
//       assertEquals("S", direction);
//   }

//   @Test
//   void testEchoEAST() {
//       map.setCurrentHeading(DroneHeading.EAST);
//       String decision = moveToGround.getNextDecision();
//       String direction = new JSONObject(decision).getJSONObject("parameters").getString("direction");
//       assertEquals("E", direction);
//   }

//   @Test
//   void testEchoWEST() {
//       map.setCurrentHeading(DroneHeading.WEST);
//       String decision = moveToGround.getNextDecision();
//       String direction = new JSONObject(decision).getJSONObject("parameters").getString("direction");
//       assertEquals("W", direction);
//   }




//   @Test
//   void testInvalidRangeDoesNotEndPhase() {
//       JSONObject echoResponse = new JSONObject().put("extras", new JSONObject().put("range", -1));
//       moveToGround.updateState(echoResponse);
//       assertFalse(moveToGround.reachedEnd());
//   }

//   @Test
//   void testBehaviorReflectsInternalState() {
//     moveToGround.updateState(new JSONObject().put("extras", new JSONObject().put("range", 3)));
//     String firstAction = moveToGround.getNextDecision();
//     assertTrue(new JSONObject(firstAction).getString("action").equals("echo"));
//     String secondAction = moveToGround.getNextDecision();
//     assertTrue(new JSONObject(secondAction).getString("action").equals("fly"));
//     moveToGround.updateState(new JSONObject().put("extras", new JSONObject().put("range", 1)));
//     moveToGround.getNextDecision();
//     assertTrue(moveToGround.reachedEnd());
//   }
// }
