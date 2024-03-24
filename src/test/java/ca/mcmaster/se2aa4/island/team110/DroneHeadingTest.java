package ca.mcmaster.se2aa4.island.team110;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import ca.mcmaster.se2aa4.island.team110.Aerial.DroneHeading;

public class DroneHeadingTest {

    @Test
    void testTurnFromNorthToEast() {
        DroneHeading expectedHeading = DroneHeading.EAST;
        DroneHeading droneHeading = DroneHeading.NORTH;
        DroneHeading actualHeading = droneHeading.turn("RIGHT");
        assertEquals(expectedHeading, actualHeading);
    }

    @Test
    void testTurnFromNorthToWest() {
        DroneHeading expectedHeading = DroneHeading.WEST;
        DroneHeading droneHeading = DroneHeading.NORTH;
        DroneHeading actualHeading = droneHeading.turn("LEFT");
        assertEquals(expectedHeading, actualHeading);
    }

    @Test
    void testTurnFromEastToNorth() {
        DroneHeading expectedHeading = DroneHeading.NORTH;
        DroneHeading droneHeading = DroneHeading.EAST;
        DroneHeading actualHeading = droneHeading.turn("LEFT");
        assertEquals(expectedHeading, actualHeading);
    }

    @Test
    void testTurnFromEastToSouth() {
        DroneHeading expectedHeading = DroneHeading.SOUTH;
        DroneHeading droneHeading = DroneHeading.EAST;
        DroneHeading actualHeading = droneHeading.turn("RIGHT");
        assertEquals(expectedHeading, actualHeading);
    }

    @Test
    void testTurnFromSouthToEast() {
        DroneHeading expectedHeading = DroneHeading.EAST;
        DroneHeading droneHeading = DroneHeading.SOUTH;
        DroneHeading actualHeading = droneHeading.turn("LEFT");
        assertEquals(expectedHeading, actualHeading);
    }

    @Test
    void testTurnFromSouthToWest() {
        DroneHeading expectedHeading = DroneHeading.WEST;
        DroneHeading droneHeading = DroneHeading.SOUTH;
        DroneHeading actualHeading = droneHeading.turn("RIGHT");
        assertEquals(expectedHeading, actualHeading);
    }

    @Test
    void testTurnFromWestToNorth() {
        DroneHeading expectedHeading = DroneHeading.NORTH;
        DroneHeading droneHeading = DroneHeading.WEST;
        DroneHeading actualHeading = droneHeading.turn("RIGHT");
        assertEquals(expectedHeading, actualHeading);
    }

    @Test
    void testTurnFromWestToSouth() {
        DroneHeading expectedHeading = DroneHeading.SOUTH;
        DroneHeading droneHeading = DroneHeading.WEST;
        DroneHeading actualHeading = droneHeading.turn("LEFT");
        assertEquals(expectedHeading, actualHeading);
    }

}