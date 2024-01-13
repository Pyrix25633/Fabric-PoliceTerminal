package net.rupyber_studios.police_terminal.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.rupyber_studios.police_terminal.PoliceTerminal;
import net.rupyber_studios.police_terminal.command.argument.StatusArgumentType;
import net.rupyber_studios.police_terminal.database.DatabaseManager;
import net.rupyber_studios.police_terminal.networking.packet.SendStatusS2CPacket;
import net.rupyber_studios.police_terminal.util.Rank;
import net.rupyber_studios.police_terminal.util.Status;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public class StatusCommand {
    public static void register(@NotNull CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess registryAccess,
                                CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("status").requires((source) -> {
            // TODO: fix, not updating
            try {
                ServerPlayerEntity player = source.getPlayer();
                if(player == null) return false;
                Rank rank = DatabaseManager.getPlayerRank(player.getUuid());
                return rank != null;
            } catch(Exception ignored) {
                return false;
            }
        }).then(CommandManager.argument("status", new StatusArgumentType()).executes((context -> {
            Status status = context.getArgument("status", Status.class);
            ServerPlayerEntity player = context.getSource().getPlayer();
            if(player == null) return 0;
            try {
                DatabaseManager.setPlayerStatus(player.getUuid(), status);
                SendStatusS2CPacket.send(player, status);
            } catch(SQLException e) {
                PoliceTerminal.LOGGER.error("Could not set status for player: ", e);
            }
            return 1;
        }))));
    }
}