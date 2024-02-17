package net.rupyber_studios.police_terminal.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;
import net.rupyber_studios.police_terminal.PoliceTerminal;
import net.rupyber_studios.police_terminal.networking.packet.*;

public class ModMessages {
    public static final Identifier SYNC_RANKS = new Identifier(PoliceTerminal.MOD_ID, "sync_ranks");
    public static final Identifier SYNC_RESPONSE_CODES = new Identifier(PoliceTerminal.MOD_ID, "sync_response_codes");
    public static final Identifier SYNC_INCIDENT_TYPES = new Identifier(PoliceTerminal.MOD_ID, "sync_incident_types");
    public static final Identifier SYNC_PLAYER_INFO = new Identifier(PoliceTerminal.MOD_ID, "sync_player_info");
    public static final Identifier SEND_STATUS = new Identifier(PoliceTerminal.MOD_ID, "send_status");
    public static final Identifier SEND_RANK = new Identifier(PoliceTerminal.MOD_ID, "send_rank");
    public static final Identifier SEND_CALLSIGN = new Identifier(PoliceTerminal.MOD_ID, "send_callsign");

    public static void registerS2CPackets() {
        ClientPlayNetworking.registerGlobalReceiver(SYNC_RANKS, SyncRanksS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(SYNC_RESPONSE_CODES, SyncResponseCodesS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(SYNC_INCIDENT_TYPES, SyncIncidentTypesS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(SYNC_PLAYER_INFO, SyncPlayerInfoS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(SEND_STATUS, SendStatusS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(SEND_RANK, SendRankS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(SEND_CALLSIGN, SendCallsignS2CPacket::receive);
    }

    public static void registerC2SPackets() {
    }
}