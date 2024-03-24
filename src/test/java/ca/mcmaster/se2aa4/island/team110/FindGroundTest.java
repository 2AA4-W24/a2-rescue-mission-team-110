// // package ca.mcmaster.se2aa4.island.team110;

// // import org.json.JSONObject;
// // import org.junit.jupiter.api.BeforeEach;
// // import org.junit.jupiter.api.Test;
// // import static org.junit.jupiter.api.Assertions.*;

// // import ca.mcmaster.se2aa4.island.team110.Phases.FindGround;
// // import ca.mcmaster.se2aa4.island.team110.Aerial.DroneHeading;

// public class FindGroundTest {
//     private FindGround findGround;
//     private TestableRelativeMap map;

//     @BeforeEach
//     void setUp() {
//         map = new TestableRelativeMap(DroneHeading.EAST);
//         findGround = new FindGround(map);
//     }

//     @Test
//     void testInitialFindGroundState() {
//         String decision = findGround.getNextDecision();
//         boolean expectedState = new JSONObject(decision).getString("action").equals("echo");
//         assertTrue(expectedState);
//     }


//     @Test
//     void testGroundDetectionSwitchesState() {
//         findGround.groundResponse(true);
//         String decision = findGround .getNextDecision();
//         String expectedAction = "heading";
//         String actualAction = new JSONObject(decision).getString("action");
//         assertEquals(expectedAction, actualAction);
//     }

//     @Test
//     void testFlyAfterGroundDetection() {
//         findGround.groundResponse(true); 
//         findGround.getNextDecision();
//         String decision = findGround.getNextDecision(); 
//         String expectedAction = "fly";
//         String actualAction = new JSONObject(decision).getString("action");
//         assertEquals(expectedAction, actualAction);
//     }

//     @Test
//     void testUpdateStateWithGroundFound() {
//         JSONObject jsonResponse = new JSONObject()
//             .put("extras", new JSONObject().put("found", "GROUND"));
//         findGround.updateState(jsonResponse);
//         String decision = findGround.getNextDecision();
//         String expectedAction = "heading";
//         String actualAction = new JSONObject(decision).getString("action");
//         assertEquals(expectedAction, actualAction);
//     }

//     @Test
//     void testUpdateStateWithNoGroundFound() {
//         JSONObject jsonResponse = new JSONObject()
//             .put("extras", new JSONObject().put("found", "OCEAN"));
//         findGround.updateState(jsonResponse);
//         String decision = findGround.getNextDecision();
//         String expectedAction = "fly";
//         String actualAction = new JSONObject(decision).getString("action");
//         assertEquals(expectedAction, actualAction);
//     }

//     @Test
//     void testFlyStateAfterEchoWithNoGround() {
//         JSONObject echoResponse = new JSONObject()
//             .put("cost", 1)
//             .put("extras", new JSONObject().put("range", 2).put("found", "OCEAN"))
//             .put("status", "OK");
//         findGround.updateState(echoResponse);
//         String decision = findGround.getNextDecision();
//         String expectedAction = "fly";
//         String actualAction = new JSONObject(decision).getString("action");
//         assertEquals(expectedAction, actualAction);
//     }

//     @Test
//     void testTurnCompletedEndsPhase() {
//         findGround.groundResponse(true);
//         findGround.getNextDecision();
//         boolean actualValue = findGround.reachedEnd();
//         assertTrue(actualValue);
//     }

//     @Test
//     void testOutOfMapEchoResponse() {
//         JSONObject echoResponse = new JSONObject()
//             .put("cost", 1)
//             .put("extras", new JSONObject().put("range", 0).put("found", "OUT_OF_RANGE"))
//             .put("status", "OK");
//         findGround.updateState(echoResponse);
//         String decision = findGround.getNextDecision();
//         boolean decision_one = decision.contains("fly");
//         boolean decision_two = decision.contains("heading");
//         assertTrue(decision_one || decision_two);
//     }
