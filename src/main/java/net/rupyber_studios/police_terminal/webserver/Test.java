package net.rupyber_studios.police_terminal.webserver;

import net.rupyber_studios.police_terminal.PoliceTerminal;
import net.rupyber_studios.police_terminal.config.ModConfig;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class Test {
    private static final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

    public static void main(String[] args) {
        ModConfig.INSTANCE = new ModConfig();
        ModConfig.INSTANCE.https = true;
        ModConfig.INSTANCE.httpsCertificate = "/home/pyrix25633/keystore.jks";
        ModConfig.INSTANCE.httpsPassword = "a1b2c3";
        PoliceTerminal.startServer(Path.of("./run/saves/New World/."));
        try {
            InputStream input = classLoader.getResourceAsStream("database/seed.sql");
            Statement statement = PoliceTerminal.connection.createStatement();
            assert input != null;
            String queries = new String(input.readAllBytes());
            for(String query : queries.split("\n\n"))
                statement.execute(query);
            statement.close();
        } catch(SQLException | IOException e) {
            PoliceTerminal.LOGGER.error("Error: ", e);
        }
        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            do {
                PoliceTerminal.LOGGER.info("Do you want to exit? (y/N): ");
            } while(!scanner.next().equalsIgnoreCase("y"));
            try {
                InputStream input = classLoader.getResourceAsStream("database/clean.sql");
                Statement statement = PoliceTerminal.connection.createStatement();
                assert input != null;
                String queries = new String(input.readAllBytes());
                for(String query : queries.split("\n\n"))
                    statement.execute(query);
                statement.close();
                System.exit(0);
            } catch(SQLException | IOException e) {
                PoliceTerminal.LOGGER.error("Error: ", e);
            }
        }).start();
    }
}