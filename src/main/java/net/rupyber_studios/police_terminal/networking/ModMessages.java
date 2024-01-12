package net.rupyber_studios.police_terminal.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;
import net.rupyber_studios.police_terminal.PoliceTerminal;
import net.rupyber_studios.police_terminal.networking.packet.SendStatusS2CPacket;
import net.rupyber_studios.police_terminal.networking.packet.SyncPlayerInfoS2CPacket;
import net.rupyber_studios.police_terminal.networking.packet.SyncRanksS2CPacket;

public class ModMessages {
    public static final Identifier SYNC_RANKS = new Identifier(PoliceTerminal.MOD_ID, "sync_ranks");
    public static final Identifier SYNC_PLAYER_INFO = new Identifier(PoliceTerminal.MOD_ID, "sync_player_info");
    public static final Identifier SEND_STATUS = new Identifier(PoliceTerminal.MOD_ID, "send_status");

    public static void registerS2CPackets() {
        ClientPlayNetworking.registerGlobalReceiver(SYNC_RANKS, SyncRanksS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(SYNC_PLAYER_INFO, SyncPlayerInfoS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(SEND_STATUS, SendStatusS2CPacket::receive);
    }

    public static void registerC2SPackets() {
    }
}