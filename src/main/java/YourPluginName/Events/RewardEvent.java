package YourPluginName.Events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RewardEvent extends Event implements Cancellable {

    private RewardEventPackage rewardEventPackage;
    private static final HandlerList HANDLERS_LIST = new HandlerList();

    public RewardEvent(RewardEventPackage rewardEventPackage) {
        this.rewardEventPackage = rewardEventPackage;
    }

    public RewardEventPackage getRewardEventPackage() {
        return rewardEventPackage;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public void setCancelled(boolean b) {

    }
}
