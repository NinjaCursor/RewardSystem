package YourPluginName.Main;

import YourPluginName.Storage.PlayerData;

public class DPlayer {

    private PlayerData self;
    private int totalPoints;
    private int level;

    public DPlayer(PlayerData self, int totalPoints, int level) {
        this.self = self;
        this.totalPoints = totalPoints;
        this.level = level;
    }

    public PlayerData getSelf() {
        return self;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public int getLevel() {
        return level;
    }
    
}
