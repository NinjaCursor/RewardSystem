package YourPluginName.Events;
import YourPluginName.Storage.PlayerData;
import org.bukkit.event.HandlerList;

public class RewardEventPackage {

    private PlayerData playerData;
    private boolean advanced;
    private int pointsGained;
    private static final HandlerList HANDLERS = new HandlerList();

    public RewardEventPackage(PlayerData playerData, int pointsGained, boolean advanced) {
        this.playerData = playerData;
        this.pointsGained = pointsGained;
        this.advanced = advanced;
    }

    public int getPointsGained() {
        return pointsGained;
    }

    public PlayerData getPlayerData() {
        return playerData;
    }

    public boolean isAdvanced() {
        return advanced;
    }
}
