package YourPluginName.Listeners;

import YourPluginName.Main.Main;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;

public class RewardListener implements Listener {
    private String addPointsMessage, totalPoints, currentLevel;

    private String loadString(String path) {
        return ChatColor.translateAlternateColorCodes('&', Main.getPlugin().getConfig().getString(path));
    }

    public RewardListener() {
        addPointsMessage = loadString("messages.points-given-event");
        totalPoints = loadString("messages.total-points");
        currentLevel = loadString("messages.level-advancement-event");
    }

    public String getAddPointsMessage(int points) {
        return addPointsMessage.replace("{points_awarded}", "" + points);
    }

    public String getTotalPoints(int points) {
        return totalPoints.replace("{total_points}", "" + points);
    }

    public String getCurrentLevel(int level) {
        return currentLevel.replace("{current_level}", "" + level);
    }

}
