package ca.mcmaster.se2aa4.island.team110;

import ca.mcmaster.se2aa4.island.team110.Records.Position;


public class RelativeMap {

    Position point;
    DroneHeading heading;

    public RelativeMap (DroneHeading start_heading) {
        this.point = new Position(0, 0);
        this.heading = start_heading;

    }

    public void updatePos() {
        switch(this.heading) {

            case NORTH:

            case SOUTH:

            case EAST:

            case WEST:
        }
    }


    
}
