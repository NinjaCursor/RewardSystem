package YourPluginName.Triggers;

import YourPluginName.Events.TimePackage;
import YourPluginName.Events.TimeRewardEvent;
import YourPluginName.Listeners.TimeRewardListener;
import YourPluginName.Main.Main;
import YourPluginName.Main.RewardHandler;
import YourPluginName.Rewards.RewardConfig;
import YourPluginName.Storage.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;
import java.util.*;

public class TimeRewardTrigger {

    private int timeChunk;
    private HashMap<UUID, TimePlayer> players;
    private int taskToken;
    private RewardConfig rewardConfig;

    public class TimePlayer implements Comparable<TimePlayer> {
        private long startTime;
        private int streak;
        private PlayerData playerData;

        public TimePlayer(long startTime, PlayerData playerData) {
            this.startTime = startTime;
            this.playerData = playerData;
            this.streak = 0;
        }

        public void increaseStreak() {
            this.streak++;
        }

        public int getStreak() {
            return this.streak;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }

        public long getStartTime() {
            return startTime;
        }

        public PlayerData getPlayerData() {
            return playerData;
        }

        public long getRemainingTicks() {
            return timeChunk - (System.currentTimeMillis() - getStartTime())/1000*20;
        }

        @Override
        public int compareTo(TimePlayer o) {
            if (getStartTime() > o.getStartTime())
                return 1;
            else if (getStartTime() < o.getStartTime())
                return -1;
            return 0;
        }
    }

    public TimeRewardTrigger() {

        this.timeChunk = 20*15;//Main.getPlugin().getConfig().getInt("time-reward.duration")*20*60;
        this.rewardConfig = new RewardConfig(200, 1, 2);
        this.players = new HashMap();

        this.taskToken = -1;

        Bukkit.getServer().getPluginManager().registerEvents(new LogInListener(), Main.getPlugin());
        Bukkit.getServer().getPluginManager().registerEvents(new TimeRewardListener(), Main.getPlugin());
    }

    public static int ticksToMinutes(int ticks) {
        return ticks / (20 * 1000);
    }

    public void countDown(long ticks, TimePlayer timePlayer) {
        Main.getLog().log("TimeReward Trigger 2");
        taskToken = Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), new Runnable() {
            @Override
            public void run() {

                Main.getLog().log("Running!");

                boolean remove = players.remove(timePlayer.getPlayerData().getUuid());
                if (remove) {
                    RewardHandler.receiveReward(timePlayer.getPlayerData(), rewardConfig, timePlayer.getStreak(), (rewardPackage) -> {
                        return new TimeRewardEvent(rewardPackage, new TimePackage(ticksToMinutes(timeChunk), timePlayer.getStreak()));
                    });
                } else {
                    //todo: handle error that should not occur
                    Main.getLog().error("countDown could not remove a player.");
                }

                timePlayer.increaseStreak();

                timePlayer.setStartTime(System.currentTimeMillis());
                players.add(timePlayer);

                TimePlayer nextPlayer = players.first();
                if (nextPlayer != null) {
                    countDown(timePlayer.getRemainingTicks(), timePlayer);
                }
            }
        }, ticks);
    }

    public class LogInListener implements Listener {

        @EventHandler
        public void onLogin(PlayerLoginEvent event) {
            Main.getLog().log("TimeReward Trigger 1");
            Bukkit.getLogger().info("TIME REWARD");
            Player player = event.getPlayer();
            UUID uuid = player.getUniqueId();
            PlayerData playerData = new PlayerData(uuid, player.getDisplayName());

            TimePlayer timePlayer = new TimePlayer(System.currentTimeMillis(), new PlayerData(uuid, player.getDisplayName()));
            players.add(timePlayer);



            if (players.size() == 1) {
                countDown(timeChunk, timePlayer);
            }
        }

        @EventHandler
        public void onLogout(PlayerQuitEvent event) {
            Player player = event.getPlayer();
            UUID uuid = player.getUniqueId();

            TimePlayer firstPlayer = players.first();

            if ()

            players.remove()

            if (firstPlayer == null)
                return;

            //if removed player was in front of waiting line, initiate count down with new first if other players exist
            if (firstPlayer.getPlayerData().getUuid() == uuid) {
                Bukkit.getScheduler().cancelTask(taskToken);
                if (players.size() > 0)
                    countDown(players.first().getRemainingTicks(), players.first());
            }

        }

    }

}
