package YourPluginName.Storage;

import YourPluginName.Main.DPlayer;
import YourPluginName.Main.Main;

import java.sql.*;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class DatabaseFunctions {

    private DatabaseTable mainTable;
    private DatabaseTable.ColumnWrapper uuidPrimaryColumn, pointsColumn, levelColumn, usernameColumn;
    private DatabaseTable logTable;
    private DatabaseTable.ColumnWrapper uuidNonPrimaryColumn, deltaPointsColumn, dateColumn, noteColumn, timeColumn, eventColumn, levelAdvancedColumn, idColumn;

    private HashMap<UUID, DPlayer> players;

    public DatabaseFunctions() throws Exception {

        uuidPrimaryColumn = new DatabaseTable.ColumnWrapper("UUID", "VARCHAR(36) NOT NULL", "PRIMARY KEY");
        usernameColumn = new DatabaseTable.ColumnWrapper("USERNAME", "VARCHAR(100) NOT NULL", "");
        pointsColumn = new DatabaseTable.ColumnWrapper("TOTAL_POINTS", "BIGINT NOT NULL", "");
        levelColumn = new DatabaseTable.ColumnWrapper("LEVEL", "INT NOT NULL", "");
        mainTable = new DatabaseTable("RewardTable", uuidPrimaryColumn, usernameColumn, pointsColumn, levelColumn);

        idColumn = new DatabaseTable.ColumnWrapper("ID", "INT NOT NULL AUTO_INCREMENT", "PRIMARY KEY");
        uuidNonPrimaryColumn = new DatabaseTable.ColumnWrapper("UUID", "VARCHAR(36) NOT NULL", "");
        deltaPointsColumn = new DatabaseTable.ColumnWrapper("POINTS", "INT NOT NULL", "");
        dateColumn = new DatabaseTable.ColumnWrapper("DATE", "DATE NOT NULL", "");
        timeColumn = new DatabaseTable.ColumnWrapper("TIME", "TIME NOT NULL", "");
        noteColumn = new DatabaseTable.ColumnWrapper("NOTE", "VARCHAR(400)", "");
        eventColumn = new DatabaseTable.ColumnWrapper("EVENT", "VARCHAR(400)", "");
        levelAdvancedColumn = new DatabaseTable.ColumnWrapper("LEVEL_ADVANCE", "INT", "");
        logTable = new DatabaseTable("RewardLogTable", idColumn, usernameColumn, uuidNonPrimaryColumn, deltaPointsColumn, dateColumn, timeColumn, noteColumn, eventColumn, levelAdvancedColumn);

        if (!mainTable.create()) {
            throw new Exception("Could not create main table.");
        }

        if (!logTable.create()) {
            throw new Exception("Could not create log table");
        }

    }

    public CompletableFuture<Boolean> loadData() {

        players = new HashMap<>();

        return CompletableFuture.supplyAsync(() -> {

            ResultSet results = SQLPool.getData((connection) -> {
                try {
                    PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + mainTable.getName());
                    return statement.executeQuery();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return null;
            });

            if (results == null)
                return false;

            try {
                while (results.next()) {
                    try {
                        String uuidString = results.getString(uuidPrimaryColumn.getName());
                        UUID uuid = UUID.fromString(uuidString);
                        String name = results.getString(usernameColumn.getName());
                        int points = results.getInt(pointsColumn.getName());
                        int level = results.getInt(levelColumn.getName());
                        PlayerData playerData = new PlayerData(uuid, name);
                        players.put(uuid, new DPlayer(playerData, points, level));

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        });
    }

    public DPlayer getPlayer(PlayerData data) {
        if (players.containsKey(data.getUuid()))
            return players.get(data.getUuid());
        DPlayer newPlayer = new DPlayer(data, 0, 0);
        players.put(data.getUuid(), newPlayer);
        return newPlayer;
    }

    public CompletableFuture<Boolean> addPoints(PlayerData playerData, int additionalPoints, boolean levelChange, String note) {

        DPlayer dPlayer = getPlayer(playerData);
        long currentTime = System.currentTimeMillis();

        DPlayer newPlayer = getNewPlayer(dPlayer, additionalPoints, levelChange);

        return CompletableFuture.supplyAsync(() -> {
                boolean success = SQLPool.sendCommand((connection) -> {
                    String mainSql = String.format("INSERT INTO %1$s (%2$s, %3$s, %4$s, %5$s) VALUES(?, ?, ?, ?) ON DUPLICATE KEY UPDATE %3$s=?, %4$s=?", mainTable.getName(), uuidPrimaryColumn.getName(), pointsColumn.getName(), levelColumn.getName(), usernameColumn.getName());
                    PreparedStatement statement = connection.prepareStatement(mainSql);

                    statement.setString(1, playerData.getUuid().toString());
                    statement.setInt(2, newPlayer.getTotalPoints());
                    statement.setInt(3, newPlayer.getLevel());

                    statement.setString(4, playerData.getName());

                    statement.setInt(5, newPlayer.getTotalPoints());
                    statement.setInt(6, newPlayer.getLevel());
                    statement.execute();

                    String logSQL = String.format("INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s, %s) VALUES(?, ?, ?, ?, ?, ?, ?, ?)", logTable.getName(), uuidNonPrimaryColumn.getName(), deltaPointsColumn.getName(), dateColumn.getName(), timeColumn.getName(), noteColumn.getName(), eventColumn.getName(), levelAdvancedColumn.getName(), usernameColumn.getName());
                    PreparedStatement stmt = connection.prepareStatement(logSQL);
                    stmt.setString(1, playerData.getUuid().toString());
                    stmt.setInt(2, additionalPoints);
                    stmt.setDate(3, new Date(currentTime));
                    stmt.setTime(4, new Time(currentTime));
                    stmt.setString(5, note);
                    stmt.setString(6, null);
                    if (levelChange)
                        stmt.setInt(7, newPlayer.getLevel());
                    else
                        stmt.setNull(7, java.sql.Types.INTEGER);
                    stmt.setString(8, playerData.getName());
                    stmt.execute();
                });

                //todo: make sure hashmap of players is thread safe

                if (success)
                    players.put(playerData.getUuid(), getNewPlayer(dPlayer, additionalPoints, levelChange));

                return success;

        });
    };

    private static DPlayer getNewPlayer(DPlayer oldPlayer, int additionalPoints, boolean levelChanged) {
        if (levelChanged)
            return new DPlayer(oldPlayer.getSelf(), oldPlayer.getTotalPoints() + additionalPoints, oldPlayer.getLevel() + 1);
        return new DPlayer(oldPlayer.getSelf(), oldPlayer.getTotalPoints() + additionalPoints, oldPlayer.getLevel());
    }

}
