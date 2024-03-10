package ca.mcmaster.se2aa4.island.team110;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class DroneHeadingTest {

    @Test
    void testTurnFromNorthToEast() {
        DroneHeading droneHeading = new DroneHeading("NORTH");
        droneHeading.turn("E");
        assertEquals("EAST", droneHeading.getCurrentDirection());
    }

    @Test
    void testTurnFromNorthToWest() {
        DroneHeading droneHeading = new DroneHeading("NORTH");
        droneHeading.turn("W");
        assertEquals("WEST", droneHeading.getCurrentDirection());
    }

    @Test
    void testTurnFromEastToNorth() {
        DroneHeading droneHeading = new DroneHeading("EAST");
        droneHeading.turn("N");
        assertEquals("NORTH", droneHeading.getCurrentDirection());
    }

    @Test
    void testTurnFromEastToSouth() {
        DroneHeading droneHeading = new DroneHeading("EAST");
        droneHeading.turn("W");
        assertEquals("SOUTH", droneHeading.getCurrentDirection());
    }

    @Test
    void testTurnFromSouthToEast() {
        DroneHeading droneHeading = new DroneHeading("SOUTH");
        droneHeading.turn("E");
        assertEquals("EAST", droneHeading.getCurrentDirection());
    }

    @Test
    void testTurnFromSouthToWest() {
        DroneHeading droneHeading = new DroneHeading("SOUTH");
        droneHeading.turn("W");
        assertEquals("WEST", droneHeading.getCurrentDirection());
    }

    @Test
    void testTurnFromWestToNorth() {
        DroneHeading droneHeading = new DroneHeading("WEST");
        droneHeading.turn("N");
        assertEquals("NORTH", droneHeading.getCurrentDirection());
    }

    @Test
    void testTurnFromWestToSouth() {
        DroneHeading droneHeading = new DroneHeading("WEST");
        droneHeading.turn("W");
        assertEquals("SOUTH", droneHeading.getCurrentDirection());
    }

    // Clockwise turning of cardinal directions
    @Test
    void testTurnClockwiseFromNorthToSouth() {
        DroneHeading droneHeading = new DroneHeading("NORTH");
        droneHeading.turn("S");
        assertEquals("EAST", droneHeading.getCurrentDirection());
    }

    @Test
    void testTurnClockwiseFromSouthToNorth() {
        DroneHeading droneHeading = new DroneHeading("SOUTH");
        droneHeading.turn("N");
        assertEquals("WEST", droneHeading.getCurrentDirection());
    }

    @Test
    void testTurnClockwiseFromEastToWest() {
        DroneHeading droneHeading = new DroneHeading("EAST");
        droneHeading.turn("W");
        assertEquals("SOUTH", droneHeading.getCurrentDirection());
    }

    @Test
    void testTurnClockwiseFromWestToEast() {
        DroneHeading droneHeading = new DroneHeading("WEST");
        droneHeading.turn("E");
        assertEquals("NORTH", droneHeading.getCurrentDirection());
    }

}