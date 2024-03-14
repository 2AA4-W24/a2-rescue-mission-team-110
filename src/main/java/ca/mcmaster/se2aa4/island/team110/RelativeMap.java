package ca.mcmaster.se2aa4.island.team110;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ca.mcmaster.se2aa4.island.team110.Records.Point;
import ca.mcmaster.se2aa4.island.team110.TileType;
import java.util.HashMap;
import java.util.Map;


public class RelativeMap {

    private final Logger logger = LogManager.getLogger();
    private Map<Point, TileType> relative_map;
    Point current_position;
    DroneHeading heading;
    
    public RelativeMap (DroneHeading heading) {
        relative_map = new HashMap<>();
        this.current_position = new Point(0, 0);
        this.heading = heading;
        relative_map.put(new Point(0, 0), TileType.UNKNOWN);

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

    public void addTile(int x, int y, TileType tileType){
        Point newTile = new Point(x, y);
        relative_map.put(newTile, tileType);

    }


    
}
