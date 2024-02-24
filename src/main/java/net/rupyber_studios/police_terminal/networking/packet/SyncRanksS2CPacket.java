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
import net.rupyber_studios.rupyber_database_api.table.Rank;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.concurrent.Semaphore;

public class SyncRanksS2CPacket {
    public static Semaphore finished = new Semaphore(0);

    public static void send(ServerPlayerEntity player) {
        PacketByteBuf data = PacketByteBufs.create();
        try {
            data.writeInt(Rank.ranks.size());
            for(int id : Rank.ranks.keySet()) {
                Rank rank = Rank.fromId(id);
                data.writeInt(id);
                data.writeString(rank.rank);
                data.writeInt(rank.color);
            }
            ServerPlayNetworking.send(player, ModMessages.SYNC_RANKS, data);
        } catch(Exception e) {
            PoliceTerminal.LOGGER.error("Could not send SyncRanksS2CPacket: ", e);
        }
    }

    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler,
                               @NotNull PacketByteBuf buf, PacketSender responseSender) {
        Rank.ranks = new HashMap<>();
        int size = buf.readInt();
        for(int i = 0; i < size; i++) {
            Rank rank = new Rank(buf.readInt(), buf.readString(), buf.readInt());
            Rank.ranks.put(rank.id, rank);
        }
        finished.release();
        PoliceTerminal.LOGGER.info("Successfully synced ranks");
    }
}