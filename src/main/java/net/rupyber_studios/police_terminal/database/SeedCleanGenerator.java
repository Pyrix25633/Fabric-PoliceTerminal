package net.rupyber_studios.police_terminal.database;

import com.github.javafaker.Faker;
import net.rupyber_studios.police_terminal.config.ModConfig;
import net.rupyber_studios.rupyber_database_api.RupyberDatabaseAPI;
import net.rupyber_studios.rupyber_database_api.table.Player;
import net.rupyber_studios.rupyber_database_api.table.Rank;
import net.rupyber_studios.rupyber_database_api.util.Callsign;
import net.rupyber_studios.rupyber_database_api.util.PlayerInfo;
import net.rupyber_studios.rupyber_database_api.util.Status;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class SeedCleanGenerator {
    private static final Random RANDOM = new Random();
    private static final Faker FAKER = new Faker();

    public static void main(String[] args) {
        ModConfig.INSTANCE = new ModConfig();
        RupyberDatabaseAPI.setPoliceTerminalConfig(ModConfig.INSTANCE);
        HashMap<UUID, Player> players = new HashMap<>();

        for(int i = 0; i < 150; i++) {
            // UUID
            UUID uuid;
            do {
                uuid = UUID.randomUUID();
            } while(players.get(uuid) != null);
            // Username
            String username;
            boolean found;
            do {
                username = generateUsername();
                found = false;
                for(Player player : players.values()) {
                    if(player.username == null) continue;
                    if(player.username.equals(username)) {
                        found = true;
                        break;
                    }
                }
            } while(found);
            // Online
            boolean online = RANDOM.nextBoolean();
            // Status, rank and callsign
            if(RANDOM.nextBoolean()) {
                Rank rank = ModConfig.INSTANCE.ranks.get(RANDOM.nextInt(ModConfig.INSTANCE.ranks.size()));
                Status status = Status.values()[RANDOM.nextInt(Status.values().length)];
                String callsign = null;
                if(RANDOM.nextBoolean()) {
                    do {
                        callsign = Callsign.createRandomCallsign();
                        found = false;
                        for(Player player : players.values()) {
                            if(player.info == null) continue;
                            if(player.info.callsign == null) continue;
                            if(player.info.callsign.equals(callsign)) {
                                found = true;
                                break;
                            }
                        }
                    } while(found);
                }
                players.put(uuid, new Player(uuid, username, online, new PlayerInfo(status, rank, callsign),
                        callsign != null && RANDOM.nextBoolean()));
            }
            else
                players.put(uuid, new Player(uuid, username, online, null, false));
        }
        // Insert
        System.out.println("INSERT INTO players (uuid, username, online, status, rankId, callsign, callsignReserved) VALUES");
        List<Player> p = players.values().stream().toList();
        for(int i = 0; i < p.size(); i++) {
            String out = getPlayerTuple(p, i);
            System.out.println(out);
        }
        // Delete
        System.out.println("DELETE FROM players WHERE uuid IN (");
        for(int i = 0; i < p.size(); i++) {
            Player player = p.get(i);
            String out = "    '" + player.uuid + "'";
            if(i < p.size() - 1) out += ",";
            System.out.println(out);
        }
        System.out.println(");");
    }

    @NotNull
    private static String getPlayerTuple(@NotNull List<Player> p, int i) {
        Player player = p.get(i);
        String out = "('" + player.uuid + "', ";
        if(player.username != null)
            out += "'" + player.username + "', ";
        else
            out += "NULL, ";
        out += String.valueOf(player.online).toUpperCase() + ", ";
        if(player.info != null) {
            out += player.info.status.getId() + ", " + player.info.rank.id + ", ";
            if(player.info.callsign != null)
                out += "'" + player.info.callsign + "', ";
            else
                out += "NULL, ";
            out += String.valueOf(player.callsignReserved).toUpperCase();
        }
        else
            out += "NULL, NULL, NULL, FALSE";
        if(i < p.size() - 1) out += "),";
        else out += ");";
        return out;
    }

    public static @Nullable String generateUsername() {
        if(RANDOM.nextInt(70) == 0) return null;
        String username;
        String prefix = FAKER.superhero().prefix();
        String name = FAKER.name().firstName();
        String number = FAKER.address().buildingNumber();
        if(RANDOM.nextBoolean()) // Both
            username = prefix + name;
        else if(RANDOM.nextBoolean()) // Only prefix
            username = prefix;
        else // Only name
            username = name;
        if(RANDOM.nextBoolean()) // Number
            username += number;
        if(username.length() > 16) return generateUsername();
        return username;
    }
}