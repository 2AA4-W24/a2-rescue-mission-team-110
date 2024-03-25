package ca.mcmaster.se2aa4.island.team110.Records;

public class Battery  {

    private int batteryLevel;

    public Battery (int batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public void updateBatteryLevel(int cost) {
        this.batteryLevel = this.batteryLevel - cost;
    }

    public int getBatteryLevel() {
        return this.batteryLevel;
    }

}