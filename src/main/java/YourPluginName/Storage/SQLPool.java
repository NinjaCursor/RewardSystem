package YourPluginName.Storage;

import YourPluginName.Main.Main;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Function;

public class SQLPool {

    private static String host, username, database, password;
    private static int port;

    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;

    static {
        try {
            FileConfiguration config = Main.getPlugin().getConfig();
            host = config.getString("mysql.host");
            port = config.getInt("mysql.port");
            database = config.getString("mysql.database");
            username = config.getString("mysql.username");
            password = config.getString("mysql.password");
        } catch (Exception e) {
            e.printStackTrace();
            Main.getLog().error("Could not load MySQL data from configuration file");
        }

        if (host == null) {
            Main.getLog().error("Host is null");
        }
        if (database == null) {
            Main.getLog().error("Database is null");
        }
        if (username == null) {
            Main.getLog().error("Username is null");
        }
        if (password == null) {
            Main.getLog().error("Password is null");
        }

        Main.getLog().log("Connecting to HOST: " + host + " on PORT: " + port + " FOR DATABASE " + database);
        config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database + "");
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName("com.mysql.jdbc.Driver");

        config.addDataSourceProperty("autoReconnect", true);
        config.addDataSourceProperty("cachePrepStmts", true);
        config.addDataSourceProperty("prepStmtCacheSize", 250);
        config.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        config.addDataSourceProperty("useServerPrepStmts", true);
        config.addDataSourceProperty("cacheResultSetMetadata", true);

        config.setConnectionTimeout(3000);

        ds = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    public static boolean sendCommand(ThrowingConsumer<Connection> function) {
        try (Connection connection = SQLPool.getConnection()) {
            try {
                connection.setAutoCommit(false);
                function.acceptThrows(connection);
                connection.commit();
                Main.getLog().log("Successful database transaction");
                return true;
            } catch (Exception e) {
                Main.getLog().error(e.getMessage());
                Main.getLog().error("Rolled back database transaction");
                connection.rollback();
            }
        } catch (SQLException e) {
            Main.getLog().error("Could not send database command. Please restart server or contact developers");
            Main.getLog().error(e.getMessage());
        }
        Main.getLog().error("Unsuccessful database transaction");
        return false;
    }

    public static ResultSet getData(Function<Connection, ResultSet> function) {
        try (Connection connection = SQLPool.getConnection()) {
            try {
                ResultSet results = function.apply(connection);
                Main.getLog().log("Successful database query");
                return results;
            } catch (Exception e) {
                Main.getLog().error("Unsuccessful database query");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Main.getLog().error("Unsuccessful database transaction");
        return null;
    }


    public static void close() {
        try {
            ds.close();
        } catch (Exception e) {
            Main.getLog().error("A SQLException was caught" + e);
        }
    }
}
