package net.rupyber_studios.police_terminal.database;

import net.rupyber_studios.police_terminal.PoliceTerminal;
import net.rupyber_studios.police_terminal.util.PlayerInfo;
import net.rupyber_studios.police_terminal.util.Rank;
import net.rupyber_studios.police_terminal.util.Status;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.UUID;

public class DatabaseManager {
    public static void createTables() throws SQLException {
        Statement statement = PoliceTerminal.connection.createStatement();

        statement.execute("""
                CREATE TABLE IF NOT EXISTS ranks (
                    id INT,
                    rank VARCHAR(16) NOT NULL,
                    color INT NOT NULL,
                    PRIMARY KEY (id),
                    UNIQUE (rank)
                );""");

        ArrayList<Integer> rankIds = new ArrayList<>();
        statement = PoliceTerminal.connection.createStatement();
        StringBuilder query = new StringBuilder("SELECT id FROM ranks WHERE id NOT IN (");
        for(Rank rank : Rank.ranks.values())
            rankIds.add(rank.id);
        if(rankIds.isEmpty()) throw new IllegalStateException("0 ranks, this is not possible");

        for(int i = 0; i < rankIds.size() - 1; i++)
            query.append(rankIds.get(i)).append(", ");
        query.append(rankIds.get(rankIds.size() - 1)).append(");");
        ResultSet result = statement.executeQuery(query.toString());
        ArrayList<Integer> missingRankIds = new ArrayList<>();
        while(result.next())
            missingRankIds.add(result.getInt("id"));

        PreparedStatement preparedStatement = PoliceTerminal.connection.prepareStatement("""
                REPLACE INTO ranks (id, rank, color) VALUES (?, ?, ?);""");
        for(Rank rank : Rank.ranks.values()) {
            preparedStatement.setInt(1, rank.id);
            preparedStatement.setString(2, rank.rank);
            preparedStatement.setInt(3, rank.color);
            preparedStatement.execute();
        }

        statement.execute("""
                CREATE TABLE IF NOT EXISTS players (
                    uuid CHAR(36),
                    username VARCHAR(16) NULL,
                    online BOOLEAN NOT NULL DEFAULT TRUE,
                    status INT NULL DEFAULT NULL,
                    rankId INT NULL DEFAULT NULL,
                    callsign VARCHAR(16) NULL DEFAULT NULL,
                    callsignReserved BOOLEAN NOT NULL DEFAULT FALSE,
                    PRIMARY KEY (uuid),
                    UNIQUE (username),
                    UNIQUE (callsign),
                    FOREIGN KEY (rankId) REFERENCES ranks(id)
                );""");

        preparedStatement = PoliceTerminal.connection.prepareStatement("""
                UPDATE players SET rankId=? WHERE rankId=?;""");
        for(int rankId : missingRankIds) {
            Integer nearestRankId = 0;
            for(int existingRankId : rankIds) {
                if(existingRankId < rankId && (existingRankId - rankId) < (nearestRankId - rankId))
                    nearestRankId = existingRankId;
            }
            if(nearestRankId == 0) nearestRankId = null;
            preparedStatement.setObject(1, nearestRankId);
            preparedStatement.setInt(2, rankId);
        }

        preparedStatement = PoliceTerminal.connection.prepareStatement("""
                DELETE FROM ranks WHERE id=?;""");
        for(int rankId : missingRankIds) {
            preparedStatement.setInt(1, rankId);
            preparedStatement.execute();
        }

        statement.close();
        preparedStatement.close();
    }

    public static void insertOrUpdatePlayer(@NotNull UUID player, @NotNull String username) throws SQLException {
        // Searching for a record with the same username
        PreparedStatement preparedStatement = PoliceTerminal.connection.prepareStatement("""
                SELECT uuid FROM players WHERE username=?;""");
        preparedStatement.setString(1, username);
        ResultSet result = preparedStatement.executeQuery();
        if(result.next()) {
            String uuid = result.getString("uuid");
            if(uuid.equals(player.toString())) {
                // Same player, just update online
                preparedStatement = PoliceTerminal.connection.prepareStatement("""
                        UPDATE players SET online=TRUE WHERE uuid=?;""");
                preparedStatement.setString(1, player.toString());
                preparedStatement.execute();
                preparedStatement.close();
                return;
            }
            else {
                // Other player, set username to null
                preparedStatement = PoliceTerminal.connection.prepareStatement("""
                        UPDATE players SET username=NULL WHERE uuid=?;""");
                preparedStatement.setString(1, uuid);
                preparedStatement.execute();
            }
        }
        preparedStatement = PoliceTerminal.connection.prepareStatement("""
                SELECT uuid FROM players WHERE uuid=?;""");
        preparedStatement.setString(1, player.toString());
        result = preparedStatement.executeQuery();
        if(result.next()) {
            // Player is already in database, just update online
            preparedStatement = PoliceTerminal.connection.prepareStatement("""
                        UPDATE players SET online=TRUE WHERE uuid=?;""");
            preparedStatement.setString(1, player.toString());
            preparedStatement.execute();
        }
        else {
            // Player has to be inserted
            preparedStatement = PoliceTerminal.connection.prepareStatement("""
                    INSERT INTO players (uuid, username) VALUES (?, ?);""");
            preparedStatement.setString(1, player.toString());
            preparedStatement.setString(2, username);
            preparedStatement.execute();
        }
        preparedStatement.close();
    }

    public static void handlePlayerDisconnection(@NotNull UUID player) throws SQLException {
        PreparedStatement preparedStatement = PoliceTerminal.connection.prepareStatement("""
                SELECT rankId, callsignReserved FROM players WHERE uuid=?;""");
        preparedStatement.setString(1, player.toString());
        ResultSet result = preparedStatement.executeQuery();
        if(!result.next()) return;
        // If it has a rank, reset status and callsign
        if(result.getInt("rankId") != 0) {
            String queryString;
            if(result.getBoolean("callsignReserved"))
                queryString = "UPDATE players SET status=1 WHERE uuid=?;";
            else
                queryString = "UPDATE players SET status=1, callsign=NULL WHERE uuid=?;";
            preparedStatement = PoliceTerminal.connection.prepareStatement(queryString);
            preparedStatement.setString(1, player.toString());
            preparedStatement.execute();
        }
        preparedStatement.close();
    }

    public static void handleShutdown() throws SQLException {
        Statement statement = PoliceTerminal.connection.createStatement();
        statement.execute("UPDATE players SET online=FALSE;");
        statement.execute("UPDATE players SET callsign=NULL WHERE callsignReserved=FALSE;");
        statement.close();
    }

    public static @NotNull ArrayList<String> getAllOnlineCallsigns() throws SQLException {
        Statement statement = PoliceTerminal.connection.createStatement();
        ResultSet result = statement.executeQuery("SELECT callsign FROM players WHERE online=TRUE AND callsign IS NOT NULL;");
        ArrayList<String> callsigns = new ArrayList<>();
        while(result.next())
            callsigns.add(result.getString("callsign"));
        return callsigns;
    }

    public static boolean isCallsignInUse(String callsign) throws SQLException {
        PreparedStatement preparedStatement = PoliceTerminal.connection.prepareStatement("""
                SELECT callsign FROM players WHERE callsign=?;""");
        preparedStatement.setString(1, callsign);
        ResultSet result = preparedStatement.executeQuery();
        return result.next();
    }

    public static @NotNull String getPlayerUsername(@NotNull UUID player) throws SQLException {
        PreparedStatement preparedStatement = PoliceTerminal.connection.prepareStatement("""
                SELECT username FROM players WHERE uuid=?;""");
        preparedStatement.setString(1, player.toString());
        ResultSet result = preparedStatement.executeQuery();
        if(!result.next()) return "";
        String username = result.getString("username");
        return username != null ? username : player.toString();
    }

    public static @NotNull PlayerInfo getPlayerInfo(@NotNull UUID player) throws SQLException {
        PreparedStatement preparedStatement = PoliceTerminal.connection.prepareStatement("""
                SELECT status, rankId, callsign FROM players WHERE uuid=?;""");
        preparedStatement.setString(1, player.toString());
        ResultSet result = preparedStatement.executeQuery();
        if(!result.next()) return new PlayerInfo();
        return new PlayerInfo(Status.fromId(result.getInt("status")), Rank.fromId(result.getInt("rankId")),
                result.getString("callsign"));
    }

    public static @Nullable Status getPlayerStatus(@NotNull UUID player) throws SQLException {
        PreparedStatement preparedStatement = PoliceTerminal.connection.prepareStatement("""
                SELECT status FROM players WHERE uuid=?;""");
        preparedStatement.setString(1, player.toString());
        ResultSet result = preparedStatement.executeQuery();
        if(!result.next()) return null;
        return Status.fromId(result.getInt("status"));
    }

    public static @Nullable Rank getPlayerRank(@NotNull UUID player) throws SQLException {
        PreparedStatement preparedStatement = PoliceTerminal.connection.prepareStatement("""
                SELECT rankId FROM players WHERE uuid=?;""");
        preparedStatement.setString(1, player.toString());
        ResultSet result = preparedStatement.executeQuery();
        if(!result.next()) return null;
        return Rank.fromId(result.getInt("rankId"));
    }

    public static @Nullable String getPlayerCallsign(@NotNull UUID player) throws SQLException {
        PreparedStatement preparedStatement = PoliceTerminal.connection.prepareStatement("""
                SELECT callsign FROM players WHERE uuid=?;""");
        preparedStatement.setString(1, player.toString());
        ResultSet result = preparedStatement.executeQuery();
        if(!result.next()) return null;
        return result.getString("callsign");
    }

    public static @Nullable UUID getPlayerUuidFromCallsign(String callsign) throws SQLException {
        PreparedStatement preparedStatement = PoliceTerminal.connection.prepareStatement("""
                SELECT uuid FROM players WHERE callsign=?;""");
        preparedStatement.setString(1, callsign);
        ResultSet result = preparedStatement.executeQuery();
        if(!result.next()) return null;
        return UUID.fromString(result.getString("uuid"));
    }

    public static void setPlayerStatus(@NotNull UUID player, @Nullable Status status) throws SQLException {
        PreparedStatement preparedStatement = PoliceTerminal.connection.prepareStatement("""
                UPDATE players SET status=? WHERE uuid=?;""");
        Integer s = status != null ? status.getId() : null;
        preparedStatement.setObject(1, s);
        preparedStatement.setString(2, player.toString());
        preparedStatement.execute();
        preparedStatement.close();
    }

    public static void setPlayerRank(@NotNull UUID player, @Nullable Rank rank) throws SQLException {
        PreparedStatement preparedStatement = PoliceTerminal.connection.prepareStatement("""
                UPDATE players SET rankId=? WHERE uuid=?;""");
        Integer r = rank != null ? rank.id : null;
        preparedStatement.setObject(1, r);
        preparedStatement.setString(2, player.toString());
        preparedStatement.execute();
        preparedStatement.close();
    }

    public static void setPlayerCallsign(@NotNull UUID player, @Nullable String callsign, boolean reserved) throws SQLException {
        PreparedStatement preparedStatement = PoliceTerminal.connection.prepareStatement("""
                UPDATE players SET callsign=?, callsignReserved=? WHERE uuid=?;""");
        preparedStatement.setString(1, callsign);
        preparedStatement.setBoolean(2, reserved);
        preparedStatement.setString(3, player.toString());
        preparedStatement.execute();
        preparedStatement.close();
    }
}