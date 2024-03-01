package ca.mcmaster.se2aa4.island.team110;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import ca.mcmaster.se2aa4.island.team110.Models.Information;


import java.util.Map;

public class jsonPropsTest {

    @Test
    public void testTranslateJson() {
        JsonTranslator translator = new JsonTranslator();
        String testJson = "maps/map03.json"; 

        Information info = translator.translateJson(testJson);

       
        Map<Integer, Map<String, Object>> edgeProps = info.getEdgeProps();
        assertTrue(edgeProps.containsKey(2463), "Key 2463 should be present in edge properties");
        assertEquals(1, edgeProps.get(2463).get("riverFlow"), "riverFlow for edge 2463 should have value 1");

        
    }
}
