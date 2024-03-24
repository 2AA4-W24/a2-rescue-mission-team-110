package ca.mcmaster.se2aa4.island.team110;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import ca.mcmaster.se2aa4.island.team110.Interfaces.JSONResponseParser;

public class DefaultJSONResponseParser implements JSONResponseParser{
    private final Logger logger = LogManager.getLogger();

    public boolean echoFound(JSONObject response) {
        if (response.has("extras")) {
            JSONObject extras = response.getJSONObject("extras");
            if (extras.has("found")) {
                if ("GROUND".equals(extras.getString("found"))) {
                    return true;
                }
                else {
                    return false;
                }
            }
        }

        return false;
    }

    public int echoRange(JSONObject response) { //returns the range of the echo
        int range;
        if (response.has("extras")) {
            JSONObject extras = response.getJSONObject("extras");
            if (extras.has("range")) {
                range = extras.getInt("range");
                return range;
            }
        }
        return Integer.MAX_VALUE;


    }

    public TileType scanTile(JSONObject response) { //returns the tiletype of the scanned tile
        if (response.has("extras")) {
            JSONObject extras = response.getJSONObject("extras");
            if (extras.has("creeks")) {
                JSONArray creeks = extras.getJSONArray("creeks");
                if (!creeks.isEmpty()) {
                    return TileType.CREEK;
                    // map.addTile(TileType.CREEK);
                    // map.addCreekID(creeks.getString(0));
                }
            }
            else {
                return null;
            }
            if (extras.has("sites")) {
                JSONArray emergency_site = extras.getJSONArray("sites");
                if (!emergency_site.isEmpty()) {
                    return TileType.EMERGENCY_SITE;
                    // map.addTile(TileType.EMERGENCY_SITE);
                }
            }
            
        }
        return TileType.UNKNOWN;

    }

    public JSONArray getID(JSONObject response) {
        if (response.has("extras")) {
            JSONObject extras = response.getJSONObject("extras");
            if (extras.has("creeks")) {
                JSONArray creeks = extras.getJSONArray("creeks");
                if (!creeks.isEmpty()) {
                    return creeks;
                    // map.addTile(TileType.CREEK);
                    // map.addCreekID(creeks.getString(0));
                }
                else {
                    return null;
                }
            }
            // if (extras.has("sites")) {
            //     JSONArray emergency_site = extras.getJSONArray("sites");
            //     if (!emergency_site.isEmpty()) {
            //         return emergency_site;
            //         // map.addTile(TileType.EMERGENCY_SITE);
            //     }
            // }
        }
        return null;

    }

    @Override
    public int getCost(JSONObject response) { //returns the cost of the action
        if (response.has("cost")) {
            int cost = response.getInt("cost");
            return cost;
        }
        return 0;
    }

    
}
