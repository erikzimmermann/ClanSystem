package de.codingair.clansystem.utils.database;


import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class Updater {

    private Connection connect;

    public int insertClan(String name) throws SQLException {
        SQLException sqlex = null;
        //closing the connection is important, thus initializing an extra parameter
        int clan_id = -1;
        try {
            connect = ConnectionManager.startConnection();
            PreparedStatement insert = connect.prepareStatement("INSERT INTO `clans` (`name`) values (?)");
            PreparedStatement select = connect.prepareStatement("SELECT `clan_id` FROM `clans` WHERE name=? ORDER BY `clan_id` DESC LIMIT 1;");

            // Parameters start with 1
            insert.setString(1, name);
            insert.executeUpdate();

            select.setString(1, name);
            ResultSet resultSet = select.executeQuery();

            resultSet.next();
            clan_id = resultSet.getInt("clan_id");
        } catch (SQLException e) {
            sqlex = e;
        } finally {
            ConnectionManager.stopConnection(connect);
            connect = null;
        }
        if (sqlex != null) {
            throw sqlex;
        }
        return clan_id;
    }

    public int insertRank(String name, int clan_id) throws SQLException {
        SQLException sqlex = null;
        //closing the connection is important, thus initializing an extra parameter
        int rank_id = -1;
        try {
            connect = ConnectionManager.startConnection();

            PreparedStatement insert = connect.prepareStatement("INSERT INTO `ranks` (`name`,`clan_id`) values (?,?)");
            PreparedStatement select = connect.prepareStatement("SELECT `rank_id` FROM `ranks` WHERE name=? AND clan_id=? ORDER BY `rank_id` DESC LIMIT 1;");

            insert.setString(1, name);
            insert.setInt(2, clan_id);
            insert.executeUpdate();

            select.setString(1, name);
            select.setInt(2, clan_id);
            ResultSet resultSet = select.executeQuery();

            resultSet.next();
            rank_id = resultSet.getInt("rank_id");
        } catch (SQLException e) {
            sqlex = e;
        } finally {
            ConnectionManager.stopConnection(connect);
            connect = null;
        }
        if (sqlex != null) {
            throw sqlex;
        }
        return rank_id;
    }

    public void insertPlayer(@NotNull UUID uuid, int clan_id, int rank_id) throws SQLException {
        SQLException sqlex = null;
        try {
            connect = ConnectionManager.startConnection();
            PreparedStatement insert = connect.prepareStatement("INSERT INTO `players` (`rank_id`,`clan_id`,`rank_id`) values (?,?)");

            insert.setString(1, uuid.toString());
            insert.setInt(2, clan_id);
            if (rank_id < 0) {
                insert.setNull(3, rank_id);
            } else {
                insert.setInt(3, rank_id);
            }

            insert.executeUpdate();
        } catch (SQLException e) {
            sqlex = e;
        } finally {
            ConnectionManager.stopConnection(connect);
            connect = null;
        }
        if (sqlex != null) {
            throw sqlex;
        }
    }

    public void insertClanStatistic(int clan_id, int statistic_id) throws SQLException {
        SQLException sqlex = null;
        try {
            connect = ConnectionManager.startConnection();
            PreparedStatement insert = connect.prepareStatement("INSERT INTO `clan_statistics` (`clan_id`,`statistics_id`) values (?,?)");

            insert.setInt(1, clan_id);
            insert.setInt(1, statistic_id);

            insert.executeUpdate();
        } catch (SQLException e) {
            sqlex = e;
        } finally {
            ConnectionManager.stopConnection(connect);
            connect = null;
        }
        if (sqlex != null) {
            throw sqlex;
        }
    }

    public void insertRankPermission(int rank_id, int permission_id) throws SQLException {
        SQLException sqlex = null;
        try {
            connect = ConnectionManager.startConnection();
            PreparedStatement insert = connect.prepareStatement("INSERT INTO `rank_permission` (`rank_id`,`permission_id`) values (?,?)");

            insert.setInt(1, rank_id);
            insert.setInt(2, permission_id);

            insert.executeUpdate();
        } catch (SQLException e) {
            sqlex = e;
        } finally {
            ConnectionManager.stopConnection(connect);
            connect = null;
        }
        if (sqlex != null) {
            throw sqlex;
        }
    }

    public void updateRank(String name, int rank_id, int clan_id) throws SQLException {
        SQLException sqlex = null;
        try {
            connect = ConnectionManager.startConnection();
            PreparedStatement update = connect.prepareStatement("UPDATE `ranks` SET `name`=? WHERE `rank_id`=? AND `clan_id`=?;");

            update.setString(1, name);
            update.setInt(2, rank_id);
            update.setInt(3, clan_id);

            update.executeUpdate();
        } catch (SQLException e) {
            sqlex = e;
        } finally {
            ConnectionManager.stopConnection(connect);
            connect = null;
        }
        if (sqlex != null) {
            throw sqlex;
        }
    }

    public void updateClanMoney(int clan_id, Double money) throws SQLException {
        SQLException sqlex = null;
        try {
            connect = ConnectionManager.startConnection();
            PreparedStatement update = connect.prepareStatement("UPDATE `clans` SET `money`=? WHERE `clan_id`=?;");

            update.setDouble(1, money);
            update.setInt(2, clan_id);

            update.executeUpdate();
        } catch (SQLException e) {
            sqlex = e;
        } finally {
            ConnectionManager.stopConnection(connect);
            connect = null;
        }
        if (sqlex != null) {
            throw sqlex;
        }
    }

    public void updateClanRank(int clan_id, int rank_id) throws SQLException {
        SQLException sqlex = null;
        try {
            connect = ConnectionManager.startConnection();
            PreparedStatement update = connect.prepareStatement("UPDATE `clans` SET `rank_id`=? WHERE `clan_id`=?;");

            update.setInt(1, rank_id);
            update.setInt(2, clan_id);

            update.executeUpdate();
        } catch (SQLException e) {
            sqlex = e;
        } finally {
            ConnectionManager.stopConnection(connect);
            connect = null;
        }
        if (sqlex != null) {
            throw sqlex;
        }
    }

    public void updateClanName(int clan_id, String name) throws SQLException {
        SQLException sqlex = null;
        try {
            connect = ConnectionManager.startConnection();
            PreparedStatement update = connect.prepareStatement("UPDATE `clans` SET `name`=? WHERE `clan_id`=?;");

            update.setString(1, name);
            update.setInt(2, clan_id);

            update.executeUpdate();
        } catch (SQLException e) {
            sqlex = e;
        } finally {
            ConnectionManager.stopConnection(connect);
            connect = null;
        }
        if (sqlex != null) {
            throw sqlex;
        }
    }

    public void updatePlayerClan(@NotNull UUID uuid, int clan_id) throws SQLException {
        SQLException sqlex = null;
        try {
            connect = ConnectionManager.startConnection();
            PreparedStatement insert = connect.prepareStatement("UPDATE `players` SET `clan_id`=? WHERE `player_id`=?;");

            insert.setInt(1, clan_id);
            insert.setString(2, uuid.toString());

            insert.execute();
        } catch (SQLException e) {
            sqlex = e;
        } finally {
            ConnectionManager.stopConnection(connect);
            connect = null;
        }
        if (sqlex != null) {
            throw sqlex;
        }
    }

    public void updatePlayerRank(@NotNull UUID uuid, int rank_id) throws SQLException {
        SQLException sqlex = null;
        try {
            connect = ConnectionManager.startConnection();
            PreparedStatement insert = connect.prepareStatement("UPDATE `players` SET `rank_id`=? WHERE `player_id`=?;");

            insert.setInt(1, rank_id);
            insert.setString(2, uuid.toString());

            insert.execute();
        } catch (SQLException e) {
            sqlex = e;
        } finally {
            ConnectionManager.stopConnection(connect);
            connect = null;
        }
        if (sqlex != null) {
            throw sqlex;
        }
    }

    public void deletePlayer(@NotNull UUID uuid) throws SQLException {
        SQLException sqlex = null;
        try {
            connect = ConnectionManager.startConnection();
            PreparedStatement insert = connect.prepareStatement("DELETE FROM `players` WHERE `player_id`=?;");

            insert.setString(1, uuid.toString());

            insert.execute();
        } catch (SQLException e) {
            sqlex = e;
        } finally {
            ConnectionManager.stopConnection(connect);
            connect = null;
        }
        if (sqlex != null) {
            throw sqlex;
        }
    }

    public void deleteClan(int clan_id) throws SQLException {
        SQLException sqlex = null;
        try {
            connect = ConnectionManager.startConnection();
            PreparedStatement insert = connect.prepareStatement("DELETE FROM `clans` WHERE `clan_id`=?;");

            insert.setInt(1, clan_id);

            insert.execute();
        } catch (SQLException e) {
            sqlex = e;
        } finally {
            ConnectionManager.stopConnection(connect);
            connect = null;
        }
        if (sqlex != null) {
            throw sqlex;
        }
    }

    public void deleteClanStatistic(int clan_id, int statistics_id) throws SQLException {
        SQLException sqlex = null;
        try {
            connect = ConnectionManager.startConnection();
            PreparedStatement insert = connect.prepareStatement("DELETE FROM `clan_statistics` WHERE `clan_id`=? AND `statistics_id`=?;");

            insert.setInt(1, clan_id);
            insert.setInt(2, statistics_id);

            insert.execute();
        } catch (SQLException e) {
            sqlex = e;
        } finally {
            ConnectionManager.stopConnection(connect);
            connect = null;
        }
        if (sqlex != null) {
            throw sqlex;
        }
    }

    public void deleteRankPermission(int rank_id, int permission_id) throws SQLException {
        SQLException sqlex = null;
        try {
            connect = ConnectionManager.startConnection();
            PreparedStatement insert = connect.prepareStatement("DELETE FROM `rank_permission` WHERE `rank_id`=? AND `permission_id`=?;");

            insert.setInt(1, rank_id);
            insert.setInt(2, permission_id);

            insert.execute();
        } catch (SQLException e) {
            sqlex = e;
        } finally {
            ConnectionManager.stopConnection(connect);
            connect = null;
        }
        if (sqlex != null) {
            throw sqlex;
        }
    }

    public void deleteRank(int rank_id) throws SQLException {
        SQLException sqlex = null;
        try {
            connect = ConnectionManager.startConnection();
            PreparedStatement insert = connect.prepareStatement("DELETE FROM `ranks` WHERE `rank_id`=?;");

            insert.setInt(1, rank_id);

            insert.execute();
        } catch (SQLException e) {
            sqlex = e;
        } finally {
            ConnectionManager.stopConnection(connect);
            connect = null;
        }
        if (sqlex != null) {
            throw sqlex;
        }
    }

}
