package ca.mcmaster.se2aa4.island.team110.Interfaces;

import org.json.JSONObject;
import ca.mcmaster.se2aa4.island.team110.TileType;

public interface JSONResponseParser {
    boolean echoFound(JSONObject response);
    int echoRange(JSONObject response);
    TileType scanTile(JSONObject response);
    int getCost(JSONObject response);

}