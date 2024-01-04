package net.rupyber_studios.police_terminal.database;

import net.rupyber_studios.police_terminal.PoliceTerminal;

import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    public static void createTables() throws SQLException {
        Statement statement = PoliceTerminal.connection.createStatement();
        /*statement.execute("""
                CREATE TABLE IF NOT EXISTS cards (
                    id LONG,
                    pinHash CHAR(64) NOT NULL,
                    balance LONG NOT NULL DEFAULT 0,
                    ownerId CHAR(36) NULL,
                    PRIMARY KEY (id),
                    FOREIGN KEY (ownerId) REFERENCES players(id)
                );""");*/
    }
}