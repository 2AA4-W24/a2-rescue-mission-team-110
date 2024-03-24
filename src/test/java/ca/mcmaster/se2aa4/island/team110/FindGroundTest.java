// package ca.mcmaster.se2aa4.island.team110;

// import org.json.JSONObject;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import static org.junit.jupiter.api.Assertions.*;

// import ca.mcmaster.se2aa4.island.team110.Phases.FindGround;
// import ca.mcmaster.se2aa4.island.team110.Aerial.DroneHeading;

// public class FindGroundTest {
//   private FindGround findGround;
//   private TestableRelativeMap map;

//   @BeforeEach
//   void setUp() {
//     map = new TestableRelativeMap(DroneHeading.EAST);
//     findGround = new FindGround(map, battery);
//   }

//   @Test
//   void testInitialFindGroundState() {
//     String decision = findGround.getNextDecision();
//     assertTrue(new JSONObject(decision).getString("action").equals("echo"));
//   }


//   @Test
//   void testGroundDetectionSwitchesState() {
//       findGround.groundResponse(true);
//       assertEquals("heading", new JSONObject(findGround.getNextDecision()).getString("action"));
//   }

//   @Test
//   void testFlyAfterGroundDetection() {
//       findGround.groundResponse(true); 
//       findGround.getNextDecision(); 
//       String decision = findGround.getNextDecision(); 
//       assertTrue(new JSONObject(decision).getString("action").equals("fly"));
//   }

//   @Test
//   void testUpdateStateWithGroundFound() {
//       JSONObject jsonResponse = new JSONObject()
//           .put("extras", new JSONObject().put("found", "GROUND"));
//       findGround.updateState(jsonResponse);
//       assertEquals("heading", new JSONObject(findGround.getNextDecision()).getString("action"));
//   }

//   @Test
//   void testUpdateStateWithNoGroundFound() {
//       JSONObject jsonResponse = new JSONObject()
//           .put("extras", new JSONObject().put("found", "OCEAN"));
//       findGround.updateState(jsonResponse);
//       assertEquals("fly", new JSONObject(findGround.getNextDecision()).getString("action"));
//   }

//   @Test
//   void testFlyStateAfterEchoWithNoGround() {
//       JSONObject echoResponse = new JSONObject()
//           .put("cost", 1)
//           .put("extras", new JSONObject().put("range", 2).put("found", "OCEAN"))
//           .put("status", "OK");
//       findGround.updateState(echoResponse);
//       assertEquals("fly", new JSONObject(findGround.getNextDecision()).getString("action"));
//   }

//   @Test
//   void testTurnCompletedEndsPhase() {
//       findGround.groundResponse(true);
//       findGround.getNextDecision();
//       assertTrue(findGround.reachedEnd());
//   }

//   @Test
//   void testOutOfMapEchoResponse() {
//     JSONObject echoResponse = new JSONObject()
//         .put("cost", 1)
//         .put("extras", new JSONObject().put("range", 0).put("found", "OUT_OF_RANGE"))
//         .put("status", "OK");
//     findGround.updateState(echoResponse);
//     assertTrue(findGround.getNextDecision().contains("fly") || findGround.getNextDecision().contains("heading"));
//   }

  

// }
