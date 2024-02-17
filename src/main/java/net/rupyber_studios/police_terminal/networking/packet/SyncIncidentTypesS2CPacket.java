package net.rupyber_studios.police_terminal.networking.packet;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.rupyber_studios.police_terminal.PoliceTerminal;
import net.rupyber_studios.police_terminal.networking.ModMessages;
import net.rupyber_studios.police_terminal.util.IncidentType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.concurrent.Semaphore;

public class SyncIncidentTypesS2CPacket {
    public static Semaphore finished = new Semaphore(0);

    public static void send(ServerPlayerEntity player) {
        PacketByteBuf data = PacketByteBufs.create();
        try {
            data.writeInt(IncidentType.incidentTypes.size());
            for(int id : IncidentType.incidentTypes.keySet()) {
                IncidentType incidentType = IncidentType.fromId(id);
                data.writeInt(id);
                data.writeString(incidentType.code);
                data.writeInt(incidentType.color);
                data.writeString(incidentType.description);
            }
            ServerPlayNetworking.send(player, ModMessages.SYNC_INCIDENT_TYPES, data);
        } catch(Exception e) {
            PoliceTerminal.LOGGER.error("Could not send SyncIncidentTypesS2CPacket: ", e);
        }
    }

    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler,
                               @NotNull PacketByteBuf buf, PacketSender responseSender) {
        IncidentType.incidentTypes = new HashMap<>();
        int size = buf.readInt();
        for(int i = 0; i < size; i++) {
            IncidentType incidentType = new IncidentType(buf.readInt(), buf.readString(), buf.readInt(), buf.readString());
            IncidentType.incidentTypes.put(incidentType.id, incidentType);
        }
        finished.release();
        PoliceTerminal.LOGGER.info("Successfully synced incident types");
    }
}