package net.rupyber_studios.police_terminal.database;

import net.minecraft.util.Pair;
import net.rupyber_studios.police_terminal.PoliceTerminal;
import net.rupyber_studios.police_terminal.config.ModConfig;
import net.rupyber_studios.police_terminal.util.PlayerInfo;
import net.rupyber_studios.police_terminal.util.Rank;
import net.rupyber_studios.police_terminal.util.Status;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DatabaseSelector {
    public static @NotNull ArrayList<String> getAllOnlineCallsigns() throws SQLException {
        Statement statement = PoliceTerminal.connection.createStatement();
        ResultSet result = statement.executeQuery("""
                SELECT callsign
                FROM players
                WHERE online=TRUE AND callsign IS NOT NULL;""");
        ArrayList<String> callsigns = new ArrayList<>();
        while(result.next())
            callsigns.add(result.getString("callsign"));
        return callsigns;
    }

    public static boolean isCallsignInUse(String callsign) throws SQLException {
        PreparedStatement preparedStatement = PoliceTerminal.connection.prepareStatement("""
                SELECT callsign
                FROM players
                WHERE callsign=?;""");
        preparedStatement.setString(1, callsign);
        ResultSet result = preparedStatement.executeQuery();
        return result.next();
    }

    public static @NotNull String getPlayerUsername(@NotNull UUID player) throws SQLException {
        PreparedStatement preparedStatement = PoliceTerminal.connection.prepareStatement("""
                SELECT username
                FROM players
                WHERE uuid=?;""");
        preparedStatement.setString(1, player.toString());
        ResultSet result = preparedStatement.executeQuery();
        if(!result.next()) return "";
        String username = result.getString("username");
        return username != null ? username : player.toString();
    }

    public static @NotNull PlayerInfo getPlayerInfo(@NotNull UUID player) throws SQLException {
        PreparedStatement preparedStatement = PoliceTerminal.connection.prepareStatement("""
                SELECT status, rankId, callsign
                FROM players
                WHERE uuid=?;""");
        preparedStatement.setString(1, player.toString());
        ResultSet result = preparedStatement.executeQuery();
        if(!result.next()) return new PlayerInfo();
        return new PlayerInfo(Status.fromId(result.getInt("status")), Rank.fromId(result.getInt("rankId")),
                result.getString("callsign"));
    }

    public static @Nullable Status getPlayerStatus(@NotNull UUID player) throws SQLException {
        PreparedStatement preparedStatement = PoliceTerminal.connection.prepareStatement("""
                SELECT status
                FROM players
                WHERE uuid=?;""");
        preparedStatement.setString(1, player.toString());
        ResultSet result = preparedStatement.executeQuery();
        if(!result.next()) return null;
        return Status.fromId(result.getInt("status"));
    }

    public static @Nullable Rank getPlayerRank(@NotNull UUID player) throws SQLException {
        PreparedStatement preparedStatement = PoliceTerminal.connection.prepareStatement("""
                SELECT rankId
                FROM players
                WHERE uuid=?;""");
        preparedStatement.setString(1, player.toString());
        ResultSet result = preparedStatement.executeQuery();
        if(!result.next()) return null;
        return Rank.fromId(result.getInt("rankId"));
    }

    public static @Nullable String getPlayerCallsign(@NotNull UUID player) throws SQLException {
        PreparedStatement preparedStatement = PoliceTerminal.connection.prepareStatement("""
                SELECT callsign
                FROM players
                WHERE uuid=?;""");
        preparedStatement.setString(1, player.toString());
        ResultSet result = preparedStatement.executeQuery();
        if(!result.next()) return null;
        return result.getString("callsign");
    }

    public static @Nullable String getPlayerSettings(@NotNull UUID player) throws SQLException {
        PreparedStatement preparedStatement = PoliceTerminal.connection.prepareStatement("""
                SELECT settings
                FROM players
                WHERE uuid=?;""");
        preparedStatement.setString(1, player.toString());
        ResultSet result = preparedStatement.executeQuery();
        if(!result.next()) return null;
        return result.getString("settings");
    }

    public static @Nullable UUID getPlayerUuidFromCallsign(String callsign) throws SQLException {
        PreparedStatement preparedStatement = PoliceTerminal.connection.prepareStatement("""
                SELECT uuid
                FROM players
                WHERE callsign=?;""");
        preparedStatement.setString(1, callsign);
        ResultSet result = preparedStatement.executeQuery();
        if(!result.next()) return null;
        return UUID.fromString(result.getString("uuid"));
    }

    public static @Nullable @Unmodifiable List<UUID> getPlayerUuidsFromCallsignLike(String callsign) throws SQLException {
        PreparedStatement preparedStatement = PoliceTerminal.connection.prepareStatement("""
                SELECT uuid
                FROM players
                WHERE callsign LIKE ?;""");
        preparedStatement.setString(1, callsign);
        ResultSet result = preparedStatement.executeQuery();
        if(!result.next()) return null;
        List<UUID> playerUuids = new ArrayList<>();
        while(result.next()) {
            playerUuids.add(UUID.fromString(result.getString("uuid")));
        }
        return playerUuids;
    }

    public static boolean isPlayerPasswordCorrect(@NotNull UUID player, String password) throws SQLException {
        if(password == null) return false;
        PreparedStatement preparedStatement = PoliceTerminal.connection.prepareStatement("""
                SELECT uuid
                FROM players
                WHERE uuid=? AND password=?;""");
        preparedStatement.setString(1, player.toString());
        preparedStatement.setString(2, password);
        ResultSet result = preparedStatement.executeQuery();
        return result.next();
    }

    public static boolean isPlayerTokenCorrect(@NotNull UUID player, String token) throws SQLException {
        if(token == null) return false;
        PreparedStatement preparedStatement = PoliceTerminal.connection.prepareStatement("""
                SELECT uuid
                FROM players
                WHERE uuid=? AND token=?;""");
        preparedStatement.setString(1, player.toString());
        preparedStatement.setString(2, token);
        ResultSet result = preparedStatement.executeQuery();
        return result.next();
    }

    public static int getCitizensPages() throws SQLException {
        Statement statement = PoliceTerminal.connection.createStatement();
        ResultSet result = statement.executeQuery("""
                SELECT COUNT(*) AS records FROM players;""");
        return (int)Math.ceil((double)result.getInt("records") / ModConfig.INSTANCE.recordsPerPage);
    }

    public static @NotNull JSONArray getCitizens(int page, String orderField, boolean orderAscending) throws SQLException {
        PreparedStatement preparedStatement = PoliceTerminal.connection.prepareStatement("""
                SELECT uuid, username, online
                FROM players
                ORDER BY #1 #2
                LIMIT ?, ?;"""
                .replace("#1", orderField)
                .replace("#2", orderAscending ? "ASC" : "DESC"));
        preparedStatement.setInt(1, page * ModConfig.INSTANCE.recordsPerPage);
        preparedStatement.setInt(2, ModConfig.INSTANCE.recordsPerPage);
        ResultSet result = preparedStatement.executeQuery();
        JSONArray citizens = new JSONArray();
        while(result.next()) {
            JSONObject citizen = new JSONObject();
            citizen.put("uuid", result.getString("uuid"));
            citizen.put("username", result.getString("username"));
            citizen.put("online", result.getBoolean("online"));
            citizens.put(citizen);
        }
        return citizens;
    }

    public static int getOfficersPages() throws SQLException {
        Statement statement = PoliceTerminal.connection.createStatement();
        ResultSet result = statement.executeQuery("""
                SELECT COUNT(*) AS records FROM players WHERE rankId IS NOT NULL;""");
        return (int)Math.ceil((double)result.getInt("records") / ModConfig.INSTANCE.recordsPerPage);
    }

    public static @NotNull JSONArray getOfficers(int page, String orderField, boolean orderAscending) throws SQLException {
        PreparedStatement preparedStatement = PoliceTerminal.connection.prepareStatement("""
                SELECT uuid, username, online, status, rank, r.color AS rankColor, callsign, callsignReserved
                FROM players AS p
                INNER JOIN ranks AS r
                ON p.rankId=r.id
                ORDER BY #1 #2
                LIMIT ?, ?;"""
                .replace("#1", orderField)
                .replace("#2", orderAscending ? "ASC" : "DESC"));
        preparedStatement.setInt(1, page * ModConfig.INSTANCE.recordsPerPage);
        preparedStatement.setInt(2, ModConfig.INSTANCE.recordsPerPage);
        ResultSet result = preparedStatement.executeQuery();
        JSONArray officers = new JSONArray();
        while(result.next()) {
            JSONObject officer = new JSONObject();
            officer.put("uuid", result.getString("uuid"));
            officer.put("username", result.getString("username"));
            officer.put("online", result.getBoolean("online"));
            Status status = Status.fromId(result.getInt("status"));
            assert status != null;
            Pair<String, Integer> statusData = status.getData();
            officer.put("status", statusData.getLeft());
            officer.put("statusColor", statusData.getRight());
            officer.put("rank", result.getString("rank"));
            officer.put("rankColor", result.getInt("rankColor"));
            String callsign = result.getString("callsign");
            officer.put("callsign", callsign != null ? callsign : JSONObject.NULL);
            officer.put("callsignReserved", result.getBoolean("callsignReserved"));
            officers.put(officer);
        }
        return officers;
    }
}