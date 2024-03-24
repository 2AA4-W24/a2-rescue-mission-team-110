// package ca.mcmaster.se2aa4.island.team110;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import static org.junit.jupiter.api.Assertions.*;
// import org.json.JSONObject;
// import ca.mcmaster.se2aa4.island.team110.Phases.ReturnHome;
// import ca.mcmaster.se2aa4.island.team110.Aerial.DroneHeading;
// import ca.mcmaster.se2aa4.island.team110.Records.Point;

// public class ReturnHomeTest {
//   private ReturnHome returnHome;
//   private TestableRelativeMap map;

//   @BeforeEach
//   void setUp() {
//       map = new TestableRelativeMap(DroneHeading.NORTH);
//       returnHome = new ReturnHome(map);
//   }


//   @Test
//   void testDecisionToFlyWhenFacingCorrectDirection() {
//       map.setCurrentPosition(new Point(-2, 4));
//       map.setCurrentHeading(DroneHeading.EAST);
//       String decision = returnHome.getNextDecision();
//       assertEquals("fly", new JSONObject(decision).getString("action"));

//       map.setCurrentPosition(new Point(2, 4));
//       map.setCurrentHeading(DroneHeading.WEST);
//       returnHome.getNextDecision();
//       assertEquals("fly", new JSONObject(returnHome.getNextDecision()).getString("action"));

//       map.setCurrentPosition(new Point(0, -1));
//       map.setCurrentHeading(DroneHeading.NORTH);
//       returnHome.getNextDecision();
//       assertEquals("fly", new JSONObject(returnHome.getNextDecision()).getString("action"));

//       map.setCurrentPosition(new Point(0, 1));
//       map.setCurrentHeading(DroneHeading.SOUTH);
//       returnHome.getNextDecision();
//       assertEquals("fly", new JSONObject(returnHome.getNextDecision()).getString("action"));
//   }

//   @Test
//   void testCorrectTurnDecisionWhenNotFacingHome() {
//       map.setCurrentPosition(new Point(-1, 4));
//       map.setCurrentHeading(DroneHeading.EAST);
//       assertEquals("heading", new JSONObject(returnHome.getNextDecision()).getString("action"));

//       map.setCurrentPosition(new Point(1, 4));
//       map.setCurrentHeading(DroneHeading.EAST);
//       assertEquals("heading", new JSONObject(returnHome.getNextDecision()).getString("action"));

//       map.setCurrentPosition(new Point(0, -1));
//       map.setCurrentHeading(DroneHeading.EAST);
//       assertEquals("heading", new JSONObject(returnHome.getNextDecision()).getString("action"));

//       map.setCurrentPosition(new Point(0, 1));
//       map.setCurrentHeading(DroneHeading.EAST);
//       assertEquals("heading", new JSONObject(returnHome.getNextDecision()).getString("action"));
//   }

      

//   @Test
//   void testIsHomeAfterMovingToHome() {
//       map.setCurrentPosition(new Point(1, 0));
//       map.setCurrentHeading(DroneHeading.WEST);
//       returnHome.getNextDecision();
//       map.setCurrentPosition(new Point(0, 0));
//       returnHome.updateState(new JSONObject());
//       assertTrue(returnHome.reachedEnd());
//   }

//   @Test
//   void testIsNotHomeWhenMovingAway() {
//       map.setCurrentPosition(new Point(0, 0));
//       map.setCurrentPosition(new Point(1, 0));
//       returnHome.updateState(new JSONObject());
//       assertFalse(returnHome.reachedEnd());
//   }

  
// }
