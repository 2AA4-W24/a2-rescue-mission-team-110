package ca.mcmaster.se2aa4.island.team110;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import ca.mcmaster.se2aa4.island.team110.Aerial.DroneHeading;

public class DroneHeadingTest {

    @Test
    void testTurnFromNorthToEast() {
        DroneHeading droneHeading = DroneHeading.NORTH;
        DroneHeading newHeading = droneHeading.turn("RIGHT");
        assertEquals(DroneHeading.EAST, newHeading);
    }

    @Test
    void testTurnFromNorthToWest() {
        DroneHeading droneHeading = DroneHeading.NORTH;
        DroneHeading newHeading = droneHeading.turn("LEFT");
        assertEquals(DroneHeading.WEST, newHeading);
    }

    @Test
    void testTurnFromEastToNorth() {
        DroneHeading droneHeading = DroneHeading.EAST;
        DroneHeading newHeading = droneHeading.turn("LEFT");
        assertEquals(DroneHeading.NORTH, newHeading);
    }

    @Test
    void testTurnFromEastToSouth() {
        DroneHeading droneHeading = DroneHeading.EAST;
        DroneHeading newHeading = droneHeading.turn("RIGHT");
        assertEquals(DroneHeading.SOUTH, newHeading);
    }

    @Test
    void testTurnFromSouthToEast() {
        DroneHeading droneHeading = DroneHeading.SOUTH;
        DroneHeading newHeading = droneHeading.turn("LEFT");
        assertEquals(DroneHeading.EAST, newHeading);
    }

    @Test
    void testTurnFromSouthToWest() {
        DroneHeading droneHeading = DroneHeading.SOUTH;
        DroneHeading newHeading = droneHeading.turn("RIGHT");
        assertEquals(DroneHeading.WEST, newHeading);
    }

    @Test
    void testTurnFromWestToNorth() {
        DroneHeading droneHeading = DroneHeading.WEST;
        DroneHeading newHeading = droneHeading.turn("RIGHT");
        assertEquals(DroneHeading.NORTH, newHeading);
    }

    @Test
    void testTurnFromWestToSouth() {
        DroneHeading droneHeading = DroneHeading.WEST;
        DroneHeading newHeading = droneHeading.turn("LEFT");
        assertEquals(DroneHeading.SOUTH, newHeading);
    }

}