package ca.mcmaster.se2aa4.island.team110;


import ca.mcmaster.se2aa4.island.team110.Records.Point;
import java.util.HashMap;


public class RelativeMap {

    private HashMap<Point, String> map;
    Point current_position;
    DroneHeading heading;

    public RelativeMap (DroneHeading heading) {
        this.current_position = new Point(0, 0);
        this.heading = heading;
        map = new HashMap<>();

    }

    public void updatePos() {
        switch(this.heading) {
            case NORTH:
                this.current_position = new Point(this.current_position.x(), this.current_position.y() + 1);
            case SOUTH:
                this.current_position = new Point(this.current_position.x(), this.current_position.y() - 1);
            case EAST:
                this.current_position = new Point(this.current_position.x() + 1, this.current_position.y());
            case WEST:
                this.current_position = new Point(this.current_position.x() - 1, this.current_position.y());
        }
        
    }

    public void addTile(int x, int y, String tileType){
        Point newTile = new Point(x, y);
        map.put(newTile, tileType);

    }


    
}
