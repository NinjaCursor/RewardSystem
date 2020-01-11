package YourPluginName.Main;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class ChatManager {

    private HashMap<UUID, ArrayList<String>> queuedMessages;
    private LogInListener listener;

    public ChatManager() {
        listener = new LogInListener();
    }

    public Listener getListener() {
        return listener;
    }

    private void addToQueue(UUID uuid, String message) {
        if (!queuedMessages.containsKey(uuid)) {
            queuedMessages.put(uuid, new ArrayList());
        }
        queuedMessages.get(uuid).add(message);
    }

    public void msg(UUID uuid, String message) {
        Player player = Bukkit.getPlayer(uuid);
        if (!player.isOnline()) {
            addToQueue(uuid, message);
        } else {
            player.sendMessage(message);
        }
    }

    public void msg(UUID uuid, String message, boolean keep) {
        Player player = Bukkit.getPlayer(uuid);
        if (!player.isOnline()) {
            if (keep)
                addToQueue(uuid, message);
        } else {
            player.sendMessage(message);
        }
    }

    public class LogInListener implements Listener {

        @EventHandler
        public void onLogin(PlayerLoginEvent event) {
            Player player = event.getPlayer();
            UUID uuid = player.getUniqueId();
            if (queuedMessages.containsKey(uuid)) {
                if (queuedMessages.get(uuid).size() > 0)
                    player.sendMessage("While you were gone, you received some messages");

                for (String message : queuedMessages.get(uuid)) {
                    player.sendMessage(message);
                }
            }
        }
    }


}
