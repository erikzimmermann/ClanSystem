package de.codingair.clansystem.utils.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariPool;
import de.codingair.clansystem.spigot.ClanSystem;
import de.codingair.codingapi.files.ConfigFile;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;

public class ConnectionManager {

    private static HikariConfig hikariConfig;
    private static HikariDataSource hikariDataSource;
    private static final String DEFAULT_FILENAME = "database";
    private static final String DEFAULT_DATABASE = "clansystem";

    static {
        hikariConfig = new HikariConfig();
    }

    public ConnectionManager() throws SQLException,ParseException {


        ConfigFile file = ClanSystem.getInstance().getFileManager().getFile("config");
        FileConfiguration config = file.getConfig();
        String driver = config.getString("ClanSystem.Database.Type", "SQLite");
        
        if (driver == null) {
            throw new ParseException("You have to specify a valid database type from the given list!",2);
        }
        switch (driver) {
            case "MySQL":
                hikariConfig.setJdbcUrl("jdbc:mysql://" + config.getString("ClanSystem.Database.MySQL.Host")+"/"+ config.getString("ClanSystem.Database.MySQL.Database"));
                hikariConfig.setUsername(config.getString("ClanSystem.Database.MySQL.Username"));
                hikariConfig.setPassword(config.getString("ClanSystem.Database.MySQL.Password"));
                hikariConfig.addDataSourceProperty("useSSL","false"); //this is solely used because most admins are not capable of providing the necessary certs
                hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
                hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
                hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
                break;
            case "SQLite":
                /*
                    TODO: Add check if database file exists
                    TODO: Create file
                    TODO: Create database
                 */
                hikariConfig.setDataSourceClassName("org.sqlite.SQLiteDataSource");
                hikariConfig.addDataSourceProperty("url","jdbc:sqlite:"+DEFAULT_FILENAME+".sqlite");
                hikariConfig.setSchema(DEFAULT_DATABASE);
                break;
            default:
                throw new ParseException("You have to specify a valid database type from the given list!",2);
        }

        hikariConfig.setConnectionTimeout(2000L);
        hikariConfig.setPoolName("ClanSystem-Pool");



        try {
            createDataSource();
        } catch (HikariPool.PoolInitializationException initializationException) {
            throw new SQLException("Cannot connect to database.");
        }

        checkSchema();
    }

    public static Connection startConnection() throws SQLException {
        return hikariDataSource.getConnection();
    }

    private void createDataSource() {
        /*
            TODO: Silence hikari's logger output...
         */
        hikariDataSource = new HikariDataSource(hikariConfig);

    }

    private void checkSchema() throws SQLException {
        Creator c = new Creator();
        c.createTables();
    }

    public static void close() {
        hikariDataSource.close();
    }
}

