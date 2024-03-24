// package ca.mcmaster.se2aa4.island.team110;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import static org.junit.jupiter.api.Assertions.*;
// import org.json.JSONObject;
// import org.json.JSONArray;

// import ca.mcmaster.se2aa4.island.team110.Phases.iSecondPass;
// import ca.mcmaster.se2aa4.island.team110.Aerial.DroneHeading;

// public class iSecondPassTest {
//   private iSecondPass secondPass;
//   private TestableRelativeMap map;

//   @BeforeEach
//   void setUp() {
//       map = new TestableRelativeMap(DroneHeading.NORTH);
//       secondPass = new iSecondPass(map);
//   }

//   @Test
//   void testInitialState() {
//       String decision = secondPass.getNextDecision();
//       assertNotNull(decision);
//   }


//   @Test
//   void testCreekDiscovery() {
//       String creekID = "creek_id";
//       map.addCreekID(creekID);
//       assertTrue(map.hasCreekID(creekID));
//   }

//   @Test
//   void testEmergencySiteDiscovery() {
//       String siteID = "site_id";
//       map.addEmergencySiteID(siteID);
//       assertTrue(map.hasEmergencySite(siteID));
//   }

//   @Test
//   void testTurnAfterEcho() {
//       secondPass.updateState(new JSONObject().put("extras", new JSONObject().put("range", 1)));
//       String decision = secondPass.getNextDecision();
//       assertEquals("heading",new JSONObject(decision).getString("action") );
//   }
// }
