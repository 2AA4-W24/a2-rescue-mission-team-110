package ca.mcmaster.se2aa4.island.team110;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.json.JSONObject;
import ca.mcmaster.se2aa4.island.team110.Phases.ReturnHome;
import ca.mcmaster.se2aa4.island.team110.Aerial.DroneHeading;
import ca.mcmaster.se2aa4.island.team110.Records.Point;
import ca.mcmaster.se2aa4.island.team110.Records.Battery;

public class ReturnHomeTest {
    private ReturnHome returnHome;
    private TestableRelativeMap map;
    private Battery battery;

    @BeforeEach
    void setUp() {
        map = new TestableRelativeMap(DroneHeading.NORTH);
        battery = new Battery(1000);
        returnHome = new ReturnHome(map, battery);
    }

    

    @Test
    void testDecisionToFlyWhenFacingCorrectDirection() {
        map.setCurrentPosition(new Point(-2, 4));
        map.setCurrentHeading(DroneHeading.EAST);
        String decision = returnHome.getNextDecision();
        String expectedAction = "fly";
        String actualAction = new JSONObject(decision).getString("action");
        assertEquals(expectedAction, actualAction);

        map.setCurrentPosition(new Point(2, 4));
        map.setCurrentHeading(DroneHeading.WEST);
        decision = returnHome.getNextDecision();
        actualAction = new JSONObject(decision).getString("action");
        assertEquals(expectedAction, actualAction);

        map.setCurrentPosition(new Point(0, -1));
        map.setCurrentHeading(DroneHeading.NORTH);
        decision = returnHome.getNextDecision();
        actualAction = new JSONObject(decision).getString("action");
        assertEquals(expectedAction, actualAction);

        map.setCurrentPosition(new Point(0, 1));
        map.setCurrentHeading(DroneHeading.SOUTH);
        decision = returnHome.getNextDecision();
        actualAction = new JSONObject(decision).getString("action");
        assertEquals(expectedAction, actualAction);
    }

    @Test
    void testCorrectTurnDecisionWhenNotFacingHome() {
        map.setCurrentHeading(DroneHeading.EAST);
        map.setCurrentPosition(new Point(1, 4));
        String decision = returnHome.getNextDecision();
        String expectedAction = "heading";
        String actualAction = new JSONObject(decision).getString("action");
        assertEquals(expectedAction, actualAction);
    }

        

    @Test
    void testIsHomeAfterMovingToHome() {
        map.setCurrentPosition(new Point(1, 0));
        map.setCurrentHeading(DroneHeading.WEST);
        returnHome.getNextDecision();
        map.setCurrentPosition(new Point(0, 0));
        returnHome.updateState(new JSONObject());
        boolean reachedEnd = returnHome.reachedEnd();
        assertTrue(reachedEnd);
    }

    @Test
    void testIsNotHomeWhenMovingAway() {
        map.setCurrentPosition(new Point(0, 0));
        map.setCurrentPosition(new Point(1, 0));
        returnHome.updateState(new JSONObject());
        boolean reachedEnd = returnHome.reachedEnd();
        assertFalse(reachedEnd);
    }

    @Test
    void testArriveAtHome() {
        map.setCurrentPosition(new Point(0, 0));
        returnHome.updateState(new JSONObject());
        assertTrue(returnHome.reachedEnd());
    }


    @Test
    void testAdjustHeadingEast() {
        map.setCurrentPosition(new Point(3, 0));
        map.setCurrentHeading(DroneHeading.NORTH);
        String decision = returnHome.getNextDecision();
        assertEquals("heading", new JSONObject(decision).getString("action"));
    }

    @Test
    void testAdjustHeadingWest() {
        map.setCurrentPosition(new Point(-3, 0));
        map.setCurrentHeading(DroneHeading.SOUTH);
        String decision = returnHome.getNextDecision();
        assertEquals("heading", new JSONObject(decision).getString("action"));
    }

    @Test
    void testAdjustHeadingNorth() {
        map.setCurrentPosition(new Point(0, -3));
        map.setCurrentHeading(DroneHeading.WEST);
        String decision = returnHome.getNextDecision();
        assertEquals("heading", new JSONObject(decision).getString("action"));
    }

    @Test
    void testAdjustHeadingSouth() {
        map.setCurrentPosition(new Point(0, 3));
        map.setCurrentHeading(DroneHeading.EAST);
        String decision = returnHome.getNextDecision();
        assertEquals("heading", new JSONObject(decision).getString("action"));
    }

    @Test
    void testInitiateUTurnWhenNecessary() {
        map.setCurrentPosition(new Point(1, 0));
        map.setCurrentHeading(DroneHeading.SOUTH);
        assertEquals("heading", new JSONObject(returnHome.getNextDecision()).getString("action"));
    }

    @Test
    void testStateUpdateAfterUTurn() {
        map.setCurrentPosition(new Point(1, 0));
        map.setCurrentHeading(DroneHeading.SOUTH);
        returnHome.getNextDecision();
        returnHome.updateState(new JSONObject());
        assertEquals("heading", new JSONObject(returnHome.getNextDecision()).getString("action"));
    }

    @Test
    void testEdgeCaseDecisionMaking() {
        map.setCurrentPosition(new Point(0, -1));
        map.setCurrentHeading(DroneHeading.SOUTH);
        assertEquals("heading", new JSONObject(returnHome.getNextDecision()).getString("action"));
    }

    @Test
    void testDroneHeadingAdjustmentNearHome() {
        map.setCurrentPosition(new Point(1, 0));
        map.setCurrentHeading(DroneHeading.NORTH);
        assertEquals("heading", new JSONObject(returnHome.getNextDecision()).getString("action"));
    }

    @Test
    void testIncorrectDroneHeadingHandling() {
        map.setCurrentPosition(new Point(0, 1));
        map.setCurrentHeading(null); 
        returnHome.getNextDecision();
        assertNull(new JSONObject(returnHome.getNextDecision()).getString("parameters"));
    }

    @Test
    void testTransitionToNextPhase() {
        map.setCurrentPosition(new Point(0, 0));
        returnHome.updateState(new JSONObject());
        assertTrue(returnHome.reachedEnd());
    }




}
