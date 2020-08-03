package de.codingair.clansystem.utils.database;


/*
    TODO: Move to other class
 */

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Creator {


    private final Connection connect = null;
    private final Statement statement = null;


    public void createTables() throws SQLException {
        SQLException sqlex = null;
        try {
            // get new connection to the database
            Connection connect = ConnectionManager.startConnection();
            Statement statement = connect.createStatement();

            //set modes we need to properly set up the database
            statement.executeUpdate("SET @OLD_DEFAULT_STORAGE_ENGINE=@@default_storage_engine, default_storage_engine=INNODB;");
            statement.executeUpdate("SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;");
            statement.executeUpdate("SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;");
            statement.executeUpdate("SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';");
            //create clans
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS `clans` (" +
                    "  `clan_id` INT UNSIGNED NOT NULL AUTO_INCREMENT," +
                    "  `name` VARCHAR(255) NOT NULL," +
                    "  `exp` BIGINT NOT NULL DEFAULT 0," +
                    "  `standard_rank_id` INT UNSIGNED NULL DEFAULT NULL," +
                    "  `money` DOUBLE NULL DEFAULT 0," +
                    "  PRIMARY KEY (`clan_id`)," +
                    "  INDEX `clans_standard_rank_id_foreign` (`standard_rank_id` ASC) VISIBLE," +
                    "  UNIQUE INDEX `clan_id_UNIQUE` (`clan_id` ASC) VISIBLE," +
                    "  CONSTRAINT `clans_standard_rank_id_foreign`" +
                    "    FOREIGN KEY (`standard_rank_id`)" +
                    "    REFERENCES `ranks` (`rank_id`)" +
                    "    ON DELETE RESTRICT);");
            //create ranks
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS `ranks` (" +
                    "  `rank_id` INT UNSIGNED NOT NULL AUTO_INCREMENT," +
                    "  `name` VARCHAR(255) NOT NULL," +
                    "  `clan_id` INT UNSIGNED NOT NULL," +
                    "  PRIMARY KEY (`rank_id`)," +
                    "  INDEX `ranks_clan_id_foreign` (`clan_id` ASC) VISIBLE," +
                    "  UNIQUE INDEX `rank_id_UNIQUE` (`rank_id` ASC) VISIBLE," +
                    "  CONSTRAINT `ranks_clan_id_foreign`" +
                    "    FOREIGN KEY (`clan_id`)" +
                    "    REFERENCES `clans` (`clan_id`)" +
                    "    ON DELETE CASCADE" +
                    "    ON UPDATE RESTRICT);");

            //create players
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS `players` (" +
                    "  `player_id` CHAR(37) NOT NULL," +
                    "  `clan_id` INT UNSIGNED NULL DEFAULT NULL," +
                    "  `rank_id` INT UNSIGNED NULL DEFAULT NULL," +
                    "  PRIMARY KEY (`player_id`)," +
                    "  INDEX `players_rank_id_foreign` (`rank_id` ASC) VISIBLE," +
                    "  INDEX `players_clan_id_foreign` (`clan_id` ASC) VISIBLE," +
                    "  UNIQUE INDEX `player_id_UNIQUE` (`player_id` ASC) VISIBLE," +
                    "  CONSTRAINT `players_rank_id_foreign`" +
                    "    FOREIGN KEY (`rank_id`)" +
                    "    REFERENCES `ranks` (`rank_id`)" +
                    "    ON DELETE SET NULL," +
                    "  CONSTRAINT `players_clan_id_foreign`" +
                    "    FOREIGN KEY (`clan_id`)" +
                    "    REFERENCES `clans` (`clan_id`)" +
                    "    ON DELETE CASCADE);");

            //create rank permissions
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS `rank_permission` (" +
                    "  `rank_id` INT UNSIGNED NOT NULL," +
                    "  `permission_id` INT UNSIGNED NOT NULL," +
                    "  PRIMARY KEY (`rank_id`, `permission_id`)," +
                    "  CONSTRAINT `rank_permission_rank_id_foregin`" +
                    "    FOREIGN KEY (`rank_id`)" +
                    "    REFERENCES `ranks` (`rank_id`)" +
                    "    ON DELETE CASCADE" +
                    "    ON UPDATE NO ACTION);");

            //create clan statistics
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS `clan_statistics` (" +
                    "  `clan_id` INT UNSIGNED NOT NULL," +
                    "  `statistics_id` INT UNSIGNED NOT NULL," +
                    "  `value` BIGINT NOT NULL DEFAULT 0," +
                    "  PRIMARY KEY (`clan_id`, `statistics_id`)," +
                    "  INDEX `foreign_clan_id_idx` (`clan_id` ASC) VISIBLE," +
                    "  CONSTRAINT `foreign_clan_id`" +
                    "    FOREIGN KEY (`clan_id`)" +
                    "    REFERENCES `clans` (`clan_id`)" +
                    "    ON DELETE CASCADE" +
                    "    ON UPDATE NO ACTION);");

            //Set modes back
            statement.executeUpdate("SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;");
            statement.executeUpdate("SET SQL_MODE=@OLD_SQL_MODE;");
            statement.executeUpdate("SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;");
            statement.executeUpdate("SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;");
            statement.executeUpdate("SET default_storage_engine=@OLD_DEFAULT_STORAGE_ENGINE");

        } catch (SQLException e) {
            sqlex = e;
        } finally {
            try {

                if (statement != null) {
                    statement.close();
                }

                if (connect != null) {
                    connect.close();
                }
            } catch (SQLException e) {
                if (sqlex != null) {
                    sqlex = e;
                }
            }
        }

        if (sqlex != null) {
            throw sqlex;
        }

    }

}
