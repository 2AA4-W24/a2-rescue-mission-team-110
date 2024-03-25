package ca.mcmaster.se2aa4.island.team110;

import ca.mcmaster.se2aa4.island.team110.Aerial.DroneHeading;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ca.mcmaster.se2aa4.island.team110.Records.Point;
import ca.mcmaster.se2aa4.island.team110.TileType;
import java.util.HashMap;
import java.util.Map;


public class RelativeMap {

    private final Logger logger = LogManager.getLogger();
    private Map<Point, TileType> relative_map;
    public Map<Point, String> creek_database;
    Point current_position;
    DroneHeading current_heading;

    
    public RelativeMap (DroneHeading initial_heading) {
        relative_map = new HashMap<>();
        creek_database = new HashMap<>();
        this.current_position = new Point(0, 0);
        this.current_heading = initial_heading;
        relative_map.put(new Point(0, 0), TileType.UNKNOWN);

    }


    public void updatePos() {
        switch(this.current_heading) {
            case NORTH:
                this.current_position = new Point(this.current_position.x(), this.current_position.y() + 1);
                break;
            case SOUTH:
                this.current_position = new Point(this.current_position.x(), this.current_position.y() - 1);
                break;
            case EAST:
                this.current_position = new Point(this.current_position.x() + 1, this.current_position.y());
                break;
            case WEST:
                this.current_position = new Point(this.current_position.x() - 1, this.current_position.y());
                break;
        }
    }

    public void updatePosTurn(String direction) {
        updatePos();
        this.current_heading = this.current_heading.turn(direction);
        updatePos();
    }

    public void updatePosMoveTo(DroneHeading heading){
        this.current_heading = heading;
        updatePos();
    }

    public void addTile(TileType tileType){
        relative_map.put(this.current_position, tileType);

    }

    public void addCreekID(String creekID) {
        creek_database.put(this.current_position, creekID);

    }

    public Point getEmergencySiteLocation() {
        for (Map.Entry<Point, TileType> entry : relative_map.entrySet()) {
            if (entry.getValue() == TileType.EMERGENCY_SITE) {
                return entry.getKey(); 
            }
        }
        return null; 
    }

    public Point getClosestCreekPosition() {
        return getClosestCreek(); 
    }
    public void setCurrentPosition(int x, int y) {
        this.current_position = new Point(x, y);
        logger.info("Current position updated to x: " + x + " y: " + y);
    }
    

    private Point getClosestCreek() {
        Point closest_creek = null;
        double closest_distance = Double.MAX_VALUE;
        Point emergency_site = null;

        for (Map.Entry<Point, TileType> entry : relative_map.entrySet()) {
            Point point = entry.getKey();
            TileType tileType = entry.getValue();

            if (tileType == TileType.EMERGENCY_SITE) {
                emergency_site = point;
                break;
            }
    
        }
        
        for (Map.Entry<Point, TileType> entry : relative_map.entrySet()) {
            Point point = entry.getKey();
            TileType tileType = entry.getValue();

            if (tileType == TileType.CREEK) {
                double distance = calculateDistance(point, emergency_site);
                if (distance < closest_distance) {
                    closest_creek = point;
                    closest_distance = distance;
                }
            }
        }

        return closest_creek;
    }

    public String getClosestCreekId() {
        Point closestCreek = getClosestCreek();
        String creekID = "";
        creekID = this.creek_database.get(closestCreek);

        return creekID;

    }

    private static double calculateDistance(Point creek, Point emergency_site) {
        double deltaX = emergency_site.x() - creek.x();
        double deltaY = emergency_site.y() - creek.y();
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    } 

    public Point getCurrentPosition() { return new Point(this.current_position.x(), this.current_position.y());}

    public DroneHeading getCurrentHeading() { return this.current_heading;}

    
}
