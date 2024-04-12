package net.rupyber_studios.police_terminal.webserver;

import net.rupyber_studios.police_terminal.PoliceTerminal;
import net.rupyber_studios.police_terminal.config.ModConfig;
import net.rupyber_studios.rupyber_database_api.RupyberDatabaseAPI;
import net.rupyber_studios.rupyber_database_api.config.PoliceTerminalConfig;
import net.rupyber_studios.rupyber_database_api.table.IncidentType;
import net.rupyber_studios.rupyber_database_api.table.Rank;
import net.rupyber_studios.rupyber_database_api.table.ResponseCode;
import net.rupyber_studios.rupyber_database_api.util.Officer;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.UUID;

public class Test {
    public static void main(String[] args) throws IOException, SQLException {
        PoliceTerminalConfig.load("./run/config/police_terminal.json");
        ModConfig.INSTANCE = new ModConfig();
        ModConfig.INSTANCE.https = true;
        ModConfig.INSTANCE.httpsCertificate = "/home/pyrix25633/keystore.jks";
        ModConfig.INSTANCE.httpsPassword = "a1b2c3";
        Path worldPath = Path.of("./run/saves/New World/.");
        Rank.loadRanks();
        ResponseCode.loadResponseCodes();
        IncidentType.loadIncidentTypes();
        RupyberDatabaseAPI.connectIfNotConnected(worldPath);
        RupyberDatabaseAPI.createPoliceTerminalTables();
        RupyberDatabaseAPI.updatePoliceTerminalTablesFromConfig();
        PoliceTerminal.startServer(worldPath);
        PoliceTerminal.LOGGER.info(Officer.initPasswordWhereUuid(UUID.fromString("9a9101ac-937d-31fc-99f0-be5bc89dd1ba")));
        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            do {
                PoliceTerminal.LOGGER.info("Do you want to exit? (y/N): ");
            } while(!scanner.next().equalsIgnoreCase("y"));
            System.exit(0);
        }).start();
    }
}