package YourPluginName.Events;
import org.bukkit.event.HandlerList;

public class TimeRewardEvent extends RewardEvent {

    private static final HandlerList HANDLERS = new HandlerList();
    private RewardEventPackage rewardEventPackage;
    private TimePackage timePackage;

    public TimeRewardEvent(RewardEventPackage rewardEvent, TimePackage timePackage) {
        super(rewardEvent);
        this.timePackage = timePackage;
    }

    public TimePackage getTimePackage() {
        return this.timePackage;
    }



}
