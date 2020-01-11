package YourPluginName.Events;

import YourPluginName.Storage.PlayerData;

public class PassiveInviteRewardEvent extends RewardEvent {

    private PlayerData invitedPlayer;
    private TimePackage timePackage;

    public PassiveInviteRewardEvent(RewardEventPackage rewardEventPackage, PlayerData invitedPlayer, TimePackage timePackage) {
        super(rewardEventPackage);
        this.invitedPlayer = invitedPlayer;
        this.timePackage = timePackage;
    }

    public PlayerData getInvitedPlayer() {
        return invitedPlayer;
    }

    public TimePackage getTimePackage() {
        return timePackage;
    }
}
