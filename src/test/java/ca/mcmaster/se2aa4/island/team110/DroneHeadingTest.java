package ca.mcmaster.se2aa4.island.team110;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class DroneHeadingTest {

    @Test
    void testTurnFromNorthToEast() {
        DroneHeading droneHeading = DroneHeading.NORTH;
        droneHeading.turn("E");
        assertEquals(DroneHeading.EAST, droneHeading);
    }

    @Test
    void testTurnFromNorthToWest() {
        DroneHeading droneHeading = DroneHeading.NORTH;
        droneHeading.turn("W");
        assertEquals(DroneHeading.WEST, droneHeading);
    }

    @Test
    void testTurnFromEastToNorth() {
        DroneHeading droneHeading = DroneHeading.EAST;
        droneHeading.turn("N");
        assertEquals(DroneHeading.NORTH, droneHeading);
    }

    @Test
    void testTurnFromEastToSouth() {
        DroneHeading droneHeading = DroneHeading.EAST;
        droneHeading.turn("W");
        assertEquals(DroneHeading.SOUTH, droneHeading);
    }

    @Test
    void testTurnFromSouthToEast() {
        DroneHeading droneHeading = DroneHeading.SOUTH;
        droneHeading.turn("E");
        assertEquals(DroneHeading.EAST, droneHeading);
    }

    @Test
    void testTurnFromSouthToWest() {
        DroneHeading droneHeading = DroneHeading.SOUTH;
        droneHeading.turn("W");
        assertEquals(DroneHeading.WEST, droneHeading);
    }

    @Test
    void testTurnFromWestToNorth() {
        DroneHeading droneHeading = DroneHeading.WEST;
        droneHeading.turn("N");
        assertEquals(DroneHeading.NORTH, droneHeading);
    }

    @Test
    void testTurnFromWestToSouth() {
        DroneHeading droneHeading = DroneHeading.WEST;
        droneHeading.turn("W");
        assertEquals(DroneHeading.SOUTH, droneHeading);
    }

    // Clockwise turning of cardinal directions
    @Test
    void testTurnClockwiseFromNorthToSouth() {
        DroneHeading droneHeading = DroneHeading.NORTH;
        droneHeading.turn("S");
        assertEquals(DroneHeading.EAST, droneHeading);
    }

    @Test
    void testTurnClockwiseFromSouthToNorth() {
        DroneHeading droneHeading = DroneHeading.SOUTH;
        droneHeading.turn("N");
        assertEquals(DroneHeading.WEST, droneHeading);
    }

    @Test
    void testTurnClockwiseFromEastToWest() {
        DroneHeading droneHeading = DroneHeading.EAST;
        droneHeading.turn("W");
        assertEquals(DroneHeading.SOUTH, droneHeading);
    }

    @Test
    void testTurnClockwiseFromWestToEast() {
        DroneHeading droneHeading = DroneHeading.WEST;
        droneHeading.turn("E");
        assertEquals(DroneHeading.NORTH, droneHeading);
    }

}