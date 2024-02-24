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
import net.rupyber_studios.rupyber_database_api.table.ResponseCode;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.concurrent.Semaphore;

public class SyncResponseCodesS2CPacket {
    public static Semaphore finished = new Semaphore(0);

    public static void send(ServerPlayerEntity player) {
        PacketByteBuf data = PacketByteBufs.create();
        try {
            data.writeInt(ResponseCode.responseCodes.size());
            for(int id : ResponseCode.responseCodes.keySet()) {
                ResponseCode responseCode = ResponseCode.fromId(id);
                data.writeInt(id);
                data.writeString(responseCode.code);
                data.writeInt(responseCode.color);
                data.writeString(responseCode.description);
            }
            ServerPlayNetworking.send(player, ModMessages.SYNC_RESPONSE_CODES, data);
        } catch(Exception e) {
            PoliceTerminal.LOGGER.error("Could not send SyncResponseCodesS2CPacket: ", e);
        }
    }

    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler,
                               @NotNull PacketByteBuf buf, PacketSender responseSender) {
        ResponseCode.responseCodes = new HashMap<>();
        int size = buf.readInt();
        for(int i = 0; i < size; i++) {
            ResponseCode responseCode = new ResponseCode(buf.readInt(), buf.readString(), buf.readInt(), buf.readString());
            ResponseCode.responseCodes.put(responseCode.id, responseCode);
        }
        finished.release();
        PoliceTerminal.LOGGER.info("Successfully synced response codes");
    }
}