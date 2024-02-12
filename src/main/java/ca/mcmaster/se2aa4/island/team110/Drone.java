package ca.mcmaster.se2aa4.island.team110;

import ca.mcmaster.se2aa4.island.team110.Interfaces.UAV;

public class Drone implements UAV{

    private int battery;
    private int posX;
    private int posY;
    private String direction;

    public Drone(int battery, int posX, int posY, String direction) { 

        this.battery = battery;
        this.posX = posX;
        this.posY = posY;
        this.direction = direction;

    }

    public int getBattery() {
        return this.battery;
    }

    public int getPosX() {
        return this.posX;
    }

    public int getPosY() {
        return this.posY;
    }

    public String getDirection() {
        return this.direction;
    }
    
}
