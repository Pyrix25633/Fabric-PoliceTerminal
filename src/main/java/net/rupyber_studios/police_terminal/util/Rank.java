package net.rupyber_studios.police_terminal.util;

import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.rupyber_studios.police_terminal.config.ModConfig;

import java.util.HashMap;

public class Rank {
    public static HashMap<Integer, Rank> ranks;

    public static void loadRanks() {
        ranks = new HashMap<>();
        for(Rank rank : ModConfig.INSTANCE.ranks) {
            ranks.put(rank.id, rank);
        }
    }

    public static Rank fromId(int id) {
        return ranks.get(id);
    }

    public Rank() {}

    public Rank(int id, String rank, int color) {
        this.id = id;
        this.rank = rank;
        this.color = color;
    }

    public int id = 10;

    public String rank = "New Rank";

    @ConfigEntry.ColorPicker
    public int color = 0x5555FF;
}