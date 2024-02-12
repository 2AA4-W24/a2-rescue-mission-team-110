package ca.mcmaster.se2aa4.island.team110;

import ca.mcmaster.se2aa4.island.team110.Interfaces.Radar;
import ca.mcmaster.se2aa4.island.team110.Enums.RadarResponse;
import ca.mcmaster.se2aa4.island.team110.Enums.Directions;

public class DroneRadar implements Radar{

    private int radarRange;

    public DroneRadar(int radarRange) { 

        this.radarRange = radarRange;

    }

    @Override
    public RadarResponse sendEcho(Directions direction){

      if (direction == Directions.NORTH){
        return RadarResponse.GROUND; //sample example on how to implement the enum
      }

    }



    public int getRadarRange(){
      return radarRange;
    }

    public void setRadarRange(int radarRange){
      this.radarRange = radarRange;

    }



}