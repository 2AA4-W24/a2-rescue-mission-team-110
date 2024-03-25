package ca.mcmaster.se2aa4.island.team110.Interfaces;

public interface Controller {
    String fly();
    String turn(String direction);
    String stop();
    String move_to(String direction);
    String land(String creekId, int crew);

}