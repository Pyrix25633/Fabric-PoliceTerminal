package net.rupyber_studios.police_terminal.networking.packet;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.rupyber_studios.police_terminal.PoliceTerminal;
import net.rupyber_studios.police_terminal.database.DatabaseManager;
import net.rupyber_studios.police_terminal.networking.ModMessages;
import net.rupyber_studios.police_terminal.util.PlayerInfo;
import net.rupyber_studios.police_terminal.util.Rank;
import net.rupyber_studios.police_terminal.util.Status;
import org.jetbrains.annotations.NotNull;

public class SyncPlayerInfoS2CPacket {
    public static void send(ServerPlayerEntity player) {
        PacketByteBuf data = PacketByteBufs.create();
        try {
            PlayerInfo info = DatabaseManager.getPlayerInfo(player.getUuid());
            data.writeInt(info.rank != null ? info.status.getId() : 0);
            data.writeInt(info.rank != null ? info.rank.id : 0);
            data.writeString(info.callsign != null ? info.callsign : "");
            ServerPlayNetworking.send(player, ModMessages.SYNC_RANKS, data);
        } catch(Exception e) {
            PoliceTerminal.LOGGER.error("Could not send SyncPlayerInfoS2CPacket: ", e);
        }
    }

    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler,
                               @NotNull PacketByteBuf buf, PacketSender responseSender) {
        new Thread(() -> {
            try {
                SyncRanksS2CPacket.finished.acquire();
                PlayerInfo.info = new PlayerInfo(Status.fromId(buf.readInt()), Rank.fromId(buf.readInt()), buf.readString());
                PoliceTerminal.LOGGER.info("Successfully synced player info");
            } catch(Exception ignored) {}
        }).start();
    }
}