package net.rupyber_studios.police_terminal.webserver;

import net.rupyber_studios.police_terminal.PoliceTerminal;
import net.rupyber_studios.police_terminal.config.ModConfig;

import java.nio.file.Path;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class Test {
    public static void main(String[] args) {
        ModConfig.INSTANCE = new ModConfig();
        ModConfig.INSTANCE.port = 3000;
        ModConfig.INSTANCE.https = true;
        ModConfig.INSTANCE.httpsCertificate = "/home/pyrix25633/keystore.jks";
        ModConfig.INSTANCE.httpsPassword = "a1b2c3";
        PoliceTerminal.startServer(Path.of("./run/saves/New World/."));
        try {
            Statement statement = PoliceTerminal.connection.createStatement();
            statement.execute("""
                    INSERT INTO players (uuid, username, status, rankId, callsign, password)
                    VALUES ("00000000-0000-0000-0000-000000000000", "TestPlayer", 2, 10, "7-Adam-22", "ABC123@#");""");
            statement.close();
        } catch(SQLException e) {
            PoliceTerminal.LOGGER.error("Error: ", e);
        }
        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            do {
                PoliceTerminal.LOGGER.info("Do you want to exit? (y/N): ");
            } while(!scanner.next().equalsIgnoreCase("y"));
            try {
                Statement statement = PoliceTerminal.connection.createStatement();
                statement.execute("""
                    DELETE FROM players WHERE uuid="00000000-0000-0000-0000-000000000000";""");
                statement.close();
                System.exit(0);
            } catch(SQLException e) {
                PoliceTerminal.LOGGER.error("Error: ", e);
            }
        }).start();
    }
}