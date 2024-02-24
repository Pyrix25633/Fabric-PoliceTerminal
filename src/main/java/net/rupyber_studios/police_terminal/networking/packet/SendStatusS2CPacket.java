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
import net.rupyber_studios.rupyber_database_api.util.PlayerInfo;
import net.rupyber_studios.rupyber_database_api.util.Status;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SendStatusS2CPacket {
    public static void send(ServerPlayerEntity player, @Nullable Status status) {
        PacketByteBuf data = PacketByteBufs.create();
        data.writeInt(status != null ? status.getId() : 0);
        ServerPlayNetworking.send(player, ModMessages.SEND_STATUS, data);
    }

    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler,
                               @NotNull PacketByteBuf buf, PacketSender responseSender) {
        PlayerInfo.info.status = Status.fromId(buf.readInt());
        PoliceTerminal.LOGGER.info("Successfully received player status");
    }
}