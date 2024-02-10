package net.rupyber_studios.police_terminal.database;

import net.rupyber_studios.police_terminal.PoliceTerminal;
import net.rupyber_studios.police_terminal.util.Credentials;
import net.rupyber_studios.police_terminal.util.Rank;
import net.rupyber_studios.police_terminal.util.Status;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class DatabaseUpdater {
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

    public static @NotNull String initPlayerPassword(@NotNull UUID player) throws SQLException {
        PreparedStatement preparedStatement = PoliceTerminal.connection.prepareStatement("""
                UPDATE players SET password=?, token=NULL WHERE uuid=?;""");
        String password = Credentials.generatePassword();
        preparedStatement.setString(1, password);
        preparedStatement.setString(2, player.toString());
        preparedStatement.execute();
        preparedStatement.close();
        return password;
    }

    public static @NotNull String initPlayerToken(@NotNull UUID player) throws SQLException {
        PreparedStatement preparedStatement = PoliceTerminal.connection.prepareStatement("""
                UPDATE players SET password=NULL, token=? WHERE uuid=?;""");
        String token = Credentials.generateToken();
        preparedStatement.setString(1, token);
        preparedStatement.setString(2, player.toString());
        preparedStatement.execute();
        preparedStatement.close();
        return token;
    }
}