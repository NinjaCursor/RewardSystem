package YourPluginName.Rewards;

import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RewardConfig implements ConfigurationSerializable {

    private ArrayList<String> messages;
    private int points;
    private int growthExponent;
    private int growthMultiplier;

    public RewardConfig(Map<String, Object> serializedRewardConfig) {
        Bukkit.getLogger().info("LOADING");
        this.messages = (ArrayList<String>) serializedRewardConfig.get("messages");
        this.points = (Integer) serializedRewardConfig.get("points");
        this.growthExponent = (Integer) serializedRewardConfig.get("growth-exponent");
        this.growthMultiplier = (Integer) serializedRewardConfig.get("growth-multiplier");
    }

    public RewardConfig(int points, int growth, int growthExponent) {
        this.points = points;
        this.growthMultiplier = growth;
        this.growthExponent = growthExponent;
        this.messages = new ArrayList<>();
    }

    public ArrayList<String> getMessages() {
        return messages;
    }

    public int getPoints() {
        return points;
    }

    public double getGrowthExponent() {
        return growthExponent;
    }

    public double getGrowthMultiplier() {
        return growthMultiplier;
    }

    @Override
    public Map<String, Object> serialize() {
        HashMap<String, Object> mapSerializer = new HashMap<>();
        mapSerializer.put("points", points);
        mapSerializer.put("growth-exponent", growthExponent);
        mapSerializer.put("growth-multiplier", growthMultiplier);
        mapSerializer.put("messages", messages);

        return mapSerializer;
    }



}
