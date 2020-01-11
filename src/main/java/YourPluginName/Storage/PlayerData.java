package YourPluginName.Storage;

import java.util.UUID;

public class PlayerData {

    private UUID uuid;
    private String name;

    public PlayerData(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }
}
