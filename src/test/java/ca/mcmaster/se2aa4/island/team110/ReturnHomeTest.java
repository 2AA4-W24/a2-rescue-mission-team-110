// package ca.mcmaster.se2aa4.island.team110;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import static org.junit.jupiter.api.Assertions.*;
// import org.json.JSONObject;
// import ca.mcmaster.se2aa4.island.team110.Phases.ReturnHome;
// import ca.mcmaster.se2aa4.island.team110.Aerial.DroneHeading;
// import ca.mcmaster.se2aa4.island.team110.Records.Point;

// public class ReturnHomeTest {
//     private ReturnHome returnHome;
//     private TestableRelativeMap map;

//     @BeforeEach
//     void setUp() {
//         map = new TestableRelativeMap(DroneHeading.NORTH);
//         returnHome = new ReturnHome(map);
//     }

//     @Test
//     void testImmediateStopWhenAtHome() {
//         map.setCurrentPosition(new Point(0, 0));
        
//         String decision = returnHome.getNextDecision();
//         String expectedAction = "stop";
//         String actualAction = new JSONObject(decision).getString("action");
//         assertEquals(expectedAction, actualAction);
//     }

//     @Test
//     void testDecisionToFlyWhenFacingCorrectDirection() {
//         map.setCurrentPosition(new Point(-2, 4));
//         map.setCurrentHeading(DroneHeading.EAST);
//         String decision = returnHome.getNextDecision();
//         String expectedAction = "fly";
//         String actualAction = new JSONObject(decision).getString("action");
//         assertEquals(expectedAction, actualAction);

//         map.setCurrentPosition(new Point(2, 4));
//         map.setCurrentHeading(DroneHeading.WEST);
//         decision = returnHome.getNextDecision();
//         actualAction = new JSONObject(decision).getString("action");
//         assertEquals(expectedAction, actualAction);

//         map.setCurrentPosition(new Point(0, -1));
//         map.setCurrentHeading(DroneHeading.NORTH);
//         decision = returnHome.getNextDecision();
//         actualAction = new JSONObject(decision).getString("action");
//         assertEquals(expectedAction, actualAction);

//         map.setCurrentPosition(new Point(0, 1));
//         map.setCurrentHeading(DroneHeading.SOUTH);
//         decision = returnHome.getNextDecision();
//         actualAction = new JSONObject(decision).getString("action");
//         assertEquals(expectedAction, actualAction);
//     }

//     @Test
//     void testCorrectTurnDecisionWhenNotFacingHome() {
//         map.setCurrentHeading(DroneHeading.EAST);
//         map.setCurrentPosition(new Point(1, 4));
//         String decision = returnHome.getNextDecision();
//         String expectedAction = "heading";
//         String actualAction = new JSONObject(decision).getString("action");
//         assertEquals(expectedAction, actualAction);
//     }

        

//     @Test
//     void testIsHomeAfterMovingToHome() {
//         map.setCurrentPosition(new Point(1, 0));
//         map.setCurrentHeading(DroneHeading.WEST);
//         returnHome.getNextDecision();
//         map.setCurrentPosition(new Point(0, 0));
//         returnHome.updateState(new JSONObject());
//         boolean reachedEnd = returnHome.reachedEnd();
//         assertTrue(reachedEnd);
//     }

//     @Test
//     void testIsNotHomeWhenMovingAway() {
//         map.setCurrentPosition(new Point(0, 0));
//         map.setCurrentPosition(new Point(1, 0));
//         returnHome.updateState(new JSONObject());
//         boolean reachedEnd = returnHome.reachedEnd();
//         assertFalse(reachedEnd);
//     }


// }
