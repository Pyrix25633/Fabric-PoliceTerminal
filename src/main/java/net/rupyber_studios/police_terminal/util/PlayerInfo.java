package net.rupyber_studios.police_terminal.util;

import net.rupyber_studios.police_terminal.config.ModConfig;

public class PlayerInfo {
    public static PlayerInfo info;

    public Status status;
    public Rank rank;
    public String callsign;

    public PlayerInfo() {
        this.status = Status.OUT_OF_SERVICE;
        this.rank = ModConfig.INSTANCE.ranks.get(0);
        this.callsign = Callsign.createRandomCallsign();
    }

    public PlayerInfo(Status status, Rank rank, String callsign) {
        this.status = status;
        this.rank = rank;
        this.callsign = (callsign == null || callsign.isEmpty()) ? null : callsign;
    }
}