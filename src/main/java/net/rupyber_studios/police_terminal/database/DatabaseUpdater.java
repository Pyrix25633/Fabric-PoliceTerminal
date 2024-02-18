package net.rupyber_studios.police_terminal.database;

import net.minecraft.util.math.Vec3d;
import net.rupyber_studios.police_terminal.PoliceTerminal;
import net.rupyber_studios.police_terminal.util.Credentials;
import net.rupyber_studios.police_terminal.util.Rank;
import net.rupyber_studios.police_terminal.util.Status;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.util.UUID;

public class DatabaseUpdater {
    public static void setPlayerStatus(@NotNull UUID player, @Nullable Status status) throws SQLException {
        PreparedStatement preparedStatement = PoliceTerminal.connection.prepareStatement("""
                UPDATE players
                SET status=?
                WHERE uuid=?;""");
        Integer s = status != null ? status.getId() : null;
        preparedStatement.setObject(1, s);
        preparedStatement.setString(2, player.toString());
        preparedStatement.execute();
        preparedStatement.close();
    }

    public static void setPlayerRank(@NotNull UUID player, @Nullable Rank rank) throws SQLException {
        PreparedStatement preparedStatement = PoliceTerminal.connection.prepareStatement("""
                UPDATE players
                SET rankId=?
                WHERE uuid=?;""");
        Integer r = rank != null ? rank.id : null;
        preparedStatement.setObject(1, r);
        preparedStatement.setString(2, player.toString());
        preparedStatement.execute();
        preparedStatement.close();
    }

    public static void setPlayerCallsign(@NotNull UUID player, @Nullable String callsign, boolean reserved) throws SQLException {
        PreparedStatement preparedStatement = PoliceTerminal.connection.prepareStatement("""
                UPDATE players
                SET callsign=?, callsignReserved=?
                WHERE uuid=?;""");
        preparedStatement.setString(1, callsign);
        preparedStatement.setBoolean(2, reserved);
        preparedStatement.setString(3, player.toString());
        preparedStatement.execute();
        preparedStatement.close();
    }

    public static @NotNull String initPlayerPassword(@NotNull UUID player) throws SQLException {
        PreparedStatement preparedStatement = PoliceTerminal.connection.prepareStatement("""
                UPDATE players
                SET password=?, token=NULL
                WHERE uuid=?;""");
        String password = Credentials.generatePassword();
        preparedStatement.setString(1, password);
        preparedStatement.setString(2, player.toString());
        preparedStatement.execute();
        preparedStatement.close();
        return password;
    }

    public static @NotNull String initPlayerToken(@NotNull UUID player) throws SQLException {
        PreparedStatement preparedStatement = PoliceTerminal.connection.prepareStatement("""
                UPDATE players
                SET password=NULL, token=?
                WHERE uuid=?;""");
        String token = Credentials.generateToken();
        preparedStatement.setString(1, token);
        preparedStatement.setString(2, player.toString());
        preparedStatement.execute();
        preparedStatement.close();
        return token;
    }

    public static void createEmergencyCall(@NotNull UUID player, Vec3d pos, String description) throws SQLException {
        Statement statement = PoliceTerminal.connection.createStatement();
        ResultSet result = statement.executeQuery("""
                SELECT CURRENT_DATE;""");
        String currentDate = result.getString("CURRENT_DATE");
        PreparedStatement preparedStatement = PoliceTerminal.connection.prepareStatement("""
                SELECT number
                FROM emergencyCallNumbers
                WHERE day=?;""");
        preparedStatement.setString(1, currentDate);
        result = preparedStatement.executeQuery();
        int number;
        if(!result.next()) number = 0;
        else number = result.getInt("number");
        preparedStatement = PoliceTerminal.connection.prepareStatement("""
                SELECT id
                FROM emergencyCalls
                WHERE closed=FALSE AND callNumber=?;""");
        do {
            number++;
            preparedStatement.setInt(1, number);
            result = preparedStatement.executeQuery();
        } while(result.next());
        preparedStatement = PoliceTerminal.connection.prepareStatement("""
                REPLACE INTO emergencyCallNumbers
                (day, number)
                VALUES (?, ?);""");
        preparedStatement.setString(1, currentDate);
        preparedStatement.setInt(2, number);
        preparedStatement.execute();
        preparedStatement = PoliceTerminal.connection.prepareStatement("""
                INSERT INTO emergencyCalls
                (callNumber, locationX, locationY, locationZ, callerId, description)
                VALUES (?, ?, ?, ?, (SELECT id FROM players WHERE uuid=?), ?);""");
        preparedStatement.setInt(1, number);
        preparedStatement.setInt(2, (int)pos.x);
        preparedStatement.setInt(3, (int)pos.y);
        preparedStatement.setInt(4, (int)pos.z);
        preparedStatement.setString(5, player.toString());
        preparedStatement.setString(6, description);
        preparedStatement.execute();
    }
}