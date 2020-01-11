package YourPluginName.Events;

public class TimePackage {
    private int timePlayed, streak;

    public TimePackage(int timePlayed, int streak) {
        this.timePlayed = timePlayed;
        this.streak = streak;
    }

    public int getTimePlayed() {
        return timePlayed;
    }

    public int getStreak() {
        return streak;
    }
}
