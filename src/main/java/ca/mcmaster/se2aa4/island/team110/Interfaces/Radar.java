package ca.mcmaster.se2aa4.island.team110.Interfaces;
import ca.mcmaster.se2aa4.island.team110.Enums.Directions;
import ca.mcmaster.se2aa4.island.team110.Enums.RadarResponse;

public interface Radar {

    RadarResponse sendEcho(Directions direction);

    RadarResponse receiveEcho();
}