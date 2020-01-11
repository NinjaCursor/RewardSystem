package YourPluginName.Listeners;

import YourPluginName.Events.TimeRewardEvent;
import YourPluginName.Main.Main;
import YourPluginName.Storage.PlayerData;
import org.bukkit.event.EventHandler;

import java.util.ArrayList;

public class TimeRewardListener extends RewardListener {

    private ArrayList<String> messages;

    public TimeRewardListener() {
        messages = (ArrayList<String>) Main.getPlugin().getConfig().getStringList("time-reward.reward.message");
    }

    @EventHandler
    public void onTimeRewardEvent(TimeRewardEvent event) {
        PlayerData rewardedPlayer = event.getRewardEventPackage().getPlayerData();
        int timePlayed = event.getTimePackage().getTimePlayed();
        int streak = event.getTimePackage().getStreak();
        int addPoints = event.getRewardEventPackage().getPointsGained();
        Main.getChatManager().msg(rewardedPlayer.getUuid(), getAddPointsMessage(addPoints), false);


    }

}
