package YourPluginName.Listeners;

import YourPluginName.Events.PassiveInviteRewardEvent;
import YourPluginName.Main.Main;
import YourPluginName.Storage.PlayerData;
import org.bukkit.event.EventHandler;

public class PassiveInviteRewardListener {

    @EventHandler
    public void onPassiveInviteReward(PassiveInviteRewardEvent event) {
        PlayerData invitedPlayer = event.getInvitedPlayer();
        PlayerData rewardedPlayer = event.getRewardEventPackage().getPlayerData();
        int timePlayed = event.getTimePackage().getTimePlayed();
        int streak = event.getTimePackage().getStreak();

        Main.getChatManager().msg(rewardedPlayer.getUuid(), "You got some points bukco from passive invite reward");

    }
}
