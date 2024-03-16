package ca.mcmaster.se2aa4.island.team110;

import ca.mcmaster.se2aa4.island.team110.Aerial.DroneHeading;
import ca.mcmaster.se2aa4.island.team110.Records.Position;


public class RelativeMap {
    Position current_position;
    DroneHeading heading;

    public RelativeMap (DroneHeading heading) {
        this.current_position = new Position(0, 0);
        this.heading = heading;

    }

    public void updatePos() {
        switch(this.heading) {
            case NORTH:
                this.current_position = new Position(this.current_position.x(), this.current_position.y() + 1);
            case SOUTH:
                this.current_position = new Position(this.current_position.x(), this.current_position.y() - 1);
            case EAST:
                this.current_position = new Position(this.current_position.x() + 1, this.current_position.y());
            case WEST:
                this.current_position = new Position(this.current_position.x() - 1, this.current_position.y());
        }
        
    }


    
}
