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
        // TODO: handle ranks
        statement.execute("""
                CREATE TABLE IF NOT EXISTS players (
                    uuid CHAR(36),
                    username VARCHAR(16) NULL,
                    status INT NULL DEFAULT NULL,
                    callsign VARCHAR(16) NULL DEFAULT NULL,
                    callsignReserved BOOLEAN NOT NULL DEFAULT FALSE,
                    rank INT NULL DEFAULT NULL,
                    PRIMARY KEY (uuid),
                    UNIQUE (username),
                    UNIQUE (callsign),
                    FOREIGN KEY (rank) REFERENCES ranks(id)
                );""");
    }

    public static void insertOrUpdatePlayer(@NotNull UUID player, @NotNull String username) throws SQLException {
        PreparedStatement preparedStatement = PoliceTerminal.connection.prepareStatement("""
                REPLACE INTO players (uuid, username) VALUES (?, ?)""");
        preparedStatement.setString(1, player.toString());
        preparedStatement.setString(2, username);
        try {
            preparedStatement.execute();
        } catch(SQLException exception) {
            preparedStatement = PoliceTerminal.connection.prepareStatement("""
                    UPDATE players SET username=NULL WHERE username=?;""");
            preparedStatement.setString(1, username);
            preparedStatement.execute();
            preparedStatement = PoliceTerminal.connection.prepareStatement("""
                    INSERT INTO players (uuid, username) VALUES (?, ?);""");
            preparedStatement.setString(1, player.toString());
            preparedStatement.setString(2, username);
            preparedStatement.execute();
        }
    }

    public static void handlePlayerDisconnection(@NotNull UUID player) throws SQLException {
        PreparedStatement preparedStatement = PoliceTerminal.connection.prepareStatement("""
                SELECT rank, callsignReserved FROM players WHERE uuid=?;""");
        preparedStatement.setString(1, player.toString());
        ResultSet result = preparedStatement.executeQuery();
        if(!result.next()) return;
        // If it has a rank, reset status and callsign
        if(result.getInt("rank") != 0) {
            String queryString;
            if(result.getBoolean("callsignReserved"))
                queryString = "UPDATE players SET status=0 WHERE uuid=?;";
            else
                queryString = "UPDATE players SET status=0, callsign=NULL WHERE uuid=?;";
            preparedStatement = PoliceTerminal.connection.prepareStatement(queryString);
            preparedStatement.setString(1, player.toString());
            preparedStatement.execute();
        }
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
                SELECT status, rank, callsign FROM players WHERE uuid=?;""");
        preparedStatement.setString(1, player.toString());
        ResultSet result = preparedStatement.executeQuery();
        if(!result.next()) return new PlayerInfo();
        return new PlayerInfo(Status.fromId(result.getInt("status")), Rank.fromId(result.getInt("rank")),
                result.getString("callsign"));
    }

    public static @Nullable Rank getPlayerRank(@NotNull UUID player) throws SQLException {
        PreparedStatement preparedStatement = PoliceTerminal.connection.prepareStatement("""
                SELECT rank FROM players WHERE uuid=?;""");
        preparedStatement.setString(1, player.toString());
        ResultSet result = preparedStatement.executeQuery();
        if(!result.next()) return null;
        return Rank.fromId(result.getInt("rank"));
    }

    public static void setPlayerStatus(@NotNull UUID player, @NotNull Status status) throws SQLException {
        PreparedStatement preparedStatement = PoliceTerminal.connection.prepareStatement("""
                UPDATE players SET status=? WHERE uuid=?;""");
        preparedStatement.setInt(1, status.getId());
        preparedStatement.setString(2, player.toString());
        preparedStatement.execute();
    }
}