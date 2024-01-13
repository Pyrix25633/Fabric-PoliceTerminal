package net.rupyber_studios.police_terminal.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.rupyber_studios.police_terminal.PoliceTerminal;
import net.rupyber_studios.police_terminal.command.argument.RankArgumentType;
import net.rupyber_studios.police_terminal.config.ModConfig;
import net.rupyber_studios.police_terminal.database.DatabaseManager;
import net.rupyber_studios.police_terminal.networking.packet.SendRankS2CPacket;
import net.rupyber_studios.police_terminal.networking.packet.SendStatusS2CPacket;
import net.rupyber_studios.police_terminal.util.Rank;
import net.rupyber_studios.police_terminal.util.Status;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public class RankCommand {
    public static void register(@NotNull CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess registryAccess,
                                CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("rank").requires((source) -> {
            if(ModConfig.INSTANCE.rankCommandRequiresAdmin && !source.hasPermissionLevel(4)) return false;
            try {
                ServerPlayerEntity player = source.getPlayer();
                if(player == null) return true;
                Rank rank = DatabaseManager.getPlayerRank(source.getPlayer().getUuid());
                return (rank != null && rank.id >= ModConfig.INSTANCE.minimumRankIdForRankCommand) || source.hasPermissionLevel(4);
            } catch(Exception ignored) {
                return false;
            }
        }).then(CommandManager.argument("username", EntityArgumentType.player())
                .then(CommandManager.argument("rank", new RankArgumentType()).executes((context) -> {
                    Rank rank = context.getArgument("rank", Rank.class);
                    ServerPlayerEntity dispatchingPlayer = context.getSource().getPlayer();
                    if(!ModConfig.INSTANCE.officerCanGrantRankHigherThanHis && dispatchingPlayer != null) {
                        try {
                            Rank dispatchingPlayerRank = DatabaseManager.getPlayerRank(dispatchingPlayer.getUuid());
                            if((dispatchingPlayerRank == null || rank.id > dispatchingPlayerRank.id) &&
                                    !dispatchingPlayer.hasPermissionLevel(4)) return 0;
                        } catch(Exception ignored) {
                            return 0;
                        }
                    }
                    ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "username");
                    try {
                        DatabaseManager.setPlayerRank(player.getUuid(), rank);
                        if(DatabaseManager.getPlayerStatus(player.getUuid()) == null) {
                            DatabaseManager.setPlayerStatus(player.getUuid(), Status.OUT_OF_SERVICE);
                            SendStatusS2CPacket.send(player, Status.OUT_OF_SERVICE);
                        }
                        SendRankS2CPacket.send(player, rank);
                    } catch(SQLException e) {
                        PoliceTerminal.LOGGER.error("Could not set status for player: ", e);
                    }
                    return 1;
                }))
        ));
    }
}