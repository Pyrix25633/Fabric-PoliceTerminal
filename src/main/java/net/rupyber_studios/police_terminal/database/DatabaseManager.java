package net.rupyber_studios.police_terminal.database;

import net.rupyber_studios.police_terminal.PoliceTerminal;
import net.rupyber_studios.police_terminal.util.IncidentType;
import net.rupyber_studios.police_terminal.util.Rank;
import net.rupyber_studios.police_terminal.util.ResponseCode;
import org.jetbrains.annotations.NotNull;

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
                    rank VARCHAR(32) NOT NULL,
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
                REPLACE INTO ranks
                (id, rank, color)
                VALUES (?, ?, ?);""");
        for(Rank rank : Rank.ranks.values()) {
            preparedStatement.setInt(1, rank.id);
            preparedStatement.setString(2, rank.rank);
            preparedStatement.setInt(3, rank.color);
            preparedStatement.execute();
        }

        statement.execute("""
                CREATE TABLE IF NOT EXISTS players (
                    id INTEGER PRIMARY KEY,
                    uuid CHAR(36) NOT NULL,
                    username VARCHAR(16) NULL,
                    online BOOLEAN NOT NULL DEFAULT TRUE,
                    status INT NULL DEFAULT NULL,
                    rankId INT NULL DEFAULT NULL,
                    callsign VARCHAR(16) NULL DEFAULT NULL,
                    callsignReserved BOOLEAN NOT NULL DEFAULT FALSE,
                    password CHAR(8) NULL DEFAULT NULL,
                    token CHAR(16) NULL DEFAULT NULL,
                    settings VARCHAR(64) NOT NULL DEFAULT '{"compactMode":false,"condensedFont":false,"sharpMode":false}',
                    UNIQUE (uuid),
                    UNIQUE (username),
                    UNIQUE (callsign),
                    FOREIGN KEY (rankId) REFERENCES ranks(id)
                );""");

        preparedStatement = PoliceTerminal.connection.prepareStatement("""
                UPDATE players
                SET rankId=?
                WHERE rankId=?;""");
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
                DELETE FROM ranks
                WHERE id=?;""");
        for(int rankId : missingRankIds) {
            preparedStatement.setInt(1, rankId);
            preparedStatement.execute();
        }

        statement.execute("""
                CREATE TABLE IF NOT EXISTS emergencyCallNumbers (
                    day DATE,
                    number INT NOT NULL,
                    PRIMARY KEY (day)
                );""");

        statement.execute("""
                CREATE TABLE IF NOT EXISTS emergencyCalls (
                    id INTEGER PRIMARY KEY,
                    callNumber INT NOT NULL,
                    locationX INT NOT NULL,
                    locationY INT NOT NULL,
                    locationZ INT NOT NULL,
                    createdAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    callerId INT NOT NULL,
                    closed BOOLEAN NOT NULL DEFAULT FALSE,
                    description VARCHAR(256),
                    FOREIGN KEY (callerId) REFERENCES players(id)
                );""");

        statement.execute("""
                CREATE TABLE IF NOT EXISTS responseCodes (
                    id INT,
                    code VARCHAR(16) NOT NULL,
                    color INT NOT NULL,
                    description VARCHAR(64) NOT NULL,
                    PRIMARY KEY (id),
                    UNIQUE (code)
                );""");

        ArrayList<Integer> responseCodeIds = new ArrayList<>();
        statement = PoliceTerminal.connection.createStatement();
        query = new StringBuilder("SELECT id FROM responseCodes WHERE id NOT IN (");
        for(ResponseCode responseCode : ResponseCode.responseCodes.values())
            responseCodeIds.add(responseCode.id);
        if(responseCodeIds.isEmpty()) throw new IllegalStateException("0 response codes, this is not possible");

        for(int i = 0; i < responseCodeIds.size() - 1; i++)
            query.append(responseCodeIds.get(i)).append(", ");
        query.append(responseCodeIds.get(responseCodeIds.size() - 1)).append(");");
        result = statement.executeQuery(query.toString());
        ArrayList<Integer> missingResponseCodeIds = new ArrayList<>();
        while(result.next())
            missingResponseCodeIds.add(result.getInt("id"));

        preparedStatement = PoliceTerminal.connection.prepareStatement("""
                REPLACE INTO responseCodes
                (id, code, color, description)
                VALUES (?, ?, ?, ?);""");
        for(ResponseCode responseCode : ResponseCode.responseCodes.values()) {
            preparedStatement.setInt(1, responseCode.id);
            preparedStatement.setString(2, responseCode.code);
            preparedStatement.setInt(3, responseCode.color);
            preparedStatement.setString(4, responseCode.description);
            preparedStatement.execute();
        }

        statement.execute("""
                CREATE TABLE IF NOT EXISTS incidentTypes (
                    id INT,
                    code VARCHAR(8) NOT NULL,
                    color INT NOT NULL,
                    description VARCHAR(64) NOT NULL,
                    PRIMARY KEY (id),
                    UNIQUE (code)
                );""");

        ArrayList<Integer> incidentTypeIds = new ArrayList<>();
        statement = PoliceTerminal.connection.createStatement();
        query = new StringBuilder("SELECT id FROM incidentTypes WHERE id NOT IN (");
        for(IncidentType incidentType : IncidentType.incidentTypes.values())
            incidentTypeIds.add(incidentType.id);
        if(incidentTypeIds.isEmpty()) throw new IllegalStateException("0 incident types, this is not possible");

        for(int i = 0; i < incidentTypeIds.size() - 1; i++)
            query.append(incidentTypeIds.get(i)).append(", ");
        query.append(incidentTypeIds.get(incidentTypeIds.size() - 1)).append(");");
        result = statement.executeQuery(query.toString());
        ArrayList<Integer> missingIncidentTypeIds = new ArrayList<>();
        while(result.next())
            missingIncidentTypeIds.add(result.getInt("id"));

        preparedStatement = PoliceTerminal.connection.prepareStatement("""
                REPLACE INTO incidentTypes (id, code, color, description) VALUES (?, ?, ?, ?);""");
        for(IncidentType incidentType : IncidentType.incidentTypes.values()) {
            preparedStatement.setInt(1, incidentType.id);
            preparedStatement.setString(2, incidentType.code);
            preparedStatement.setInt(3, incidentType.color);
            preparedStatement.setString(4, incidentType.description);
            preparedStatement.execute();
        }

        statement.execute("""
                CREATE TABLE IF NOT EXISTS incidentNumbers (
                    day DATE,
                    number INT NOT NULL,
                    PRIMARY KEY (day)
                );""");

        statement.execute("""
                CREATE TABLE IF NOT EXISTS incidents (
                    id INTEGER PRIMARY KEY,
                    incidentNumber INT NOT NULL,
                    emergencyCallId INT NULL DEFAULT NULL,
                    priority INT NOT NULL,
                    responseCodeId INT NOT NULL,
                    recipients INT NOT NULL,
                    incidentTypeId INT NOT NULL,
                    locationX INT NOT NULL,
                    locationY INT NOT NULL,
                    locationZ INT NOT NULL,
                    description VARCHAR(128) NULL,
                    createdAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    createdBy INT NOT NULL,
                    closedAt DATETIME NULL DEFAULT NULL,
                    closedBy INT NULL DEFAULT NULL,
                    FOREIGN KEY (emergencyCallId) REFERENCES emergencyCalls(id),
                    FOREIGN KEY (responseCodeId) REFERENCES responseCodes(id),
                    FOREIGN KEY (incidentTypeId) REFERENCES incidentTypes(id),
                    FOREIGN KEY (createdBy) REFERENCES players(id),
                    FOREIGN KEY (closedBy) REFERENCES players(id)
                );""");

        preparedStatement = PoliceTerminal.connection.prepareStatement("""
                UPDATE incidents SET responseCodeId=? WHERE responseCodeId=?;""");
        for(int responseCodeId : missingResponseCodeIds) {
            Integer nearestResponseCodeId = 0;
            for(int existingResponseCodeId : responseCodeIds) {
                if(existingResponseCodeId < responseCodeId && (existingResponseCodeId - responseCodeId) < (nearestResponseCodeId - responseCodeId))
                    nearestResponseCodeId = existingResponseCodeId;
            }
            if(nearestResponseCodeId == 0) nearestResponseCodeId = null;
            preparedStatement.setObject(1, nearestResponseCodeId);
            preparedStatement.setInt(2, responseCodeId);
        }

        preparedStatement = PoliceTerminal.connection.prepareStatement("""
                DELETE FROM responseCodes WHERE id=?;""");
        for(int responseCodeId : missingResponseCodeIds) {
            preparedStatement.setInt(1, responseCodeId);
            preparedStatement.execute();
        }

        preparedStatement = PoliceTerminal.connection.prepareStatement("""
                UPDATE incidents SET incidentTypeId=? WHERE incidentTypeId=?;""");
        for(int incidentTypeId : missingIncidentTypeIds) {
            Integer nearestIncidentTypeId = 0;
            for(int existingIncidentTypeId : incidentTypeIds) {
                if(existingIncidentTypeId < incidentTypeId && (existingIncidentTypeId - incidentTypeId) < (nearestIncidentTypeId - incidentTypeId))
                    nearestIncidentTypeId = existingIncidentTypeId;
            }
            if(nearestIncidentTypeId == 0) nearestIncidentTypeId = null;
            preparedStatement.setObject(1, nearestIncidentTypeId);
            preparedStatement.setInt(2, incidentTypeId);
        }

        preparedStatement = PoliceTerminal.connection.prepareStatement("""
                DELETE FROM incidentTypes WHERE id=?;""");
        for(int incidentTypeId : missingIncidentTypeIds) {
            preparedStatement.setInt(1, incidentTypeId);
            preparedStatement.execute();
        }

        statement.execute("""
                CREATE TABLE IF NOT EXISTS incidentPlayers (
                    incidentId INT NOT NULL,
                    role INT NOT NULL,
                    playerId INT NOT NULL,
                    addedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    removedAt DATETIME NULL DEFAULT NULL,
                    FOREIGN KEY (incidentId) REFERENCES incidents(id),
                    FOREIGN KEY (playerId) REFERENCES players(id)
                );""");

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
                queryString = "UPDATE players SET status=1, password=NULL, token=NULL WHERE uuid=?;";
            else
                queryString = "UPDATE players SET status=1, callsign=NULL, password=NULL, token=NULL WHERE uuid=?;";
            preparedStatement = PoliceTerminal.connection.prepareStatement(queryString);
            preparedStatement.setString(1, player.toString());
            preparedStatement.execute();
        }
        preparedStatement.close();
    }

    public static void handleShutdown() throws SQLException {
        Statement statement = PoliceTerminal.connection.createStatement();
        statement.execute("UPDATE players SET online=FALSE, password=NULL, token=NULL;");
        statement.execute("UPDATE players SET callsign=NULL WHERE callsignReserved=FALSE;");
        statement.close();
    }
}