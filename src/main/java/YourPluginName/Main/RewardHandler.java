package YourPluginName.Main;

import YourPluginName.Events.RewardEvent;
import YourPluginName.Events.RewardEventPackage;
import YourPluginName.Rewards.RewardConfig;
import YourPluginName.Storage.PlayerData;
import org.bukkit.Bukkit;

import java.util.function.Function;

public class RewardHandler {

    public static void receiveReward(PlayerData player, RewardConfig config, int streak, Function<RewardEventPackage, RewardEvent> eventCreationCallback) {

        int pointsGained = 100;
        boolean advancedLevel = true;

        try {
            Main.getDataHandler().addPoints(player, 100, false, "A+ student in crafting").thenAccept((success) -> {

                Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), new Runnable() {
                    @Override
                    public void run() {
                        if (success)
                            Bukkit.getServer().getPluginManager().callEvent(eventCreationCallback.apply(new RewardEventPackage(player, pointsGained, advancedLevel)));
                    }
                });

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    };


}
