// package ca.mcmaster.se2aa4.island.team110;

// import org.json.JSONArray;
// import org.json.JSONObject;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import static org.junit.jupiter.api.Assertions.*;

// import ca.mcmaster.se2aa4.island.team110.Phases.iFirstPass;
// import ca.mcmaster.se2aa4.island.team110.Aerial.DroneHeading;


// public class iFirstPassTest {
//   private iFirstPass firstPass;
//   private TestableRelativeMap map;

//   @BeforeEach
//   void setUp() {
//       map = new TestableRelativeMap(DroneHeading.EAST);
//       firstPass = new iFirstPass(map);
//   }

//   @Test
//   void testInitialState() {
//     String decision = firstPass.getNextDecision();
//     String expectedDecision = "scan";
//     String actualDecision = new JSONObject(decision).getString("action");
//     assertEquals(expectedDecision, actualDecision);
//   }

//   @Test
//   void testScanToEchoTransition() {
//     firstPass.getNextDecision();
//     firstPass.updateState(new JSONObject().put("extras", new JSONObject()));
//     String decision = firstPass.getNextDecision();
//     String expectedDecision = "echo";
//     String actualDecision = new JSONObject(decision).getString("action");
//     assertEquals(expectedDecision, actualDecision);
//   }

//   @Test
//   void testEchoToFlyTransition() {
//     firstPass.getNextDecision();
//     firstPass.updateState(new JSONObject().put("extras", new JSONObject()));
//     firstPass.getNextDecision();
//     firstPass.updateState(new JSONObject().put("extras", new JSONObject().put("range", 2)));
//     String decision = firstPass.getNextDecision();
//     String expectedDecision = "fly";
//     String actualDecision = new JSONObject(decision).getString("action");
//     assertEquals(expectedDecision, actualDecision);
//   }

 
//   @Test
//   void testUpdateStateWithCreeks() {
//     JSONObject response = new JSONObject()
//         .put("extras", new JSONObject().put("creeks", new JSONArray().put("creek-id")));
//     firstPass.updateState(response);
//     assertNotNull(map.getCreekID());
//   }


  
  
//  }
