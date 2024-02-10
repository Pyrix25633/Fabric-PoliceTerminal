package net.rupyber_studios.police_terminal.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.rupyber_studios.police_terminal.PoliceTerminal;
import net.rupyber_studios.police_terminal.command.argument.RankArgumentType;
import net.rupyber_studios.police_terminal.config.ModConfig;
import net.rupyber_studios.police_terminal.database.DatabaseSelector;
import net.rupyber_studios.police_terminal.database.DatabaseUpdater;
import net.rupyber_studios.police_terminal.networking.packet.SendRankS2CPacket;
import net.rupyber_studios.police_terminal.networking.packet.SendStatusS2CPacket;
import net.rupyber_studios.police_terminal.util.Rank;
import net.rupyber_studios.police_terminal.util.Status;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public class RankCommand {
    private static final Text WAS_PROMOTED_TEXT = Text.translatable("commands.rank.success.was_promoted");
    private static final Text WAS_DEMOTED_TEXT = Text.translatable("commands.rank.success.was_demoted");
    private static final Text PROMOTED_TEXT = Text.translatable("commands.rank.success.promoted");
    private static final Text DEMOTED_TEXT = Text.translatable("commands.rank.success.demoted");
    private static final Text FROM_TEXT = Text.translatable("commands.rank.success.from");
    private static final Text TO_TEXT = Text.translatable("commands.rank.success.to");
    private static final Rank CIVILLIAN_RANK = new Rank(0, "Civilian", 0xAAAAAA);

    public static void register(@NotNull CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess registryAccess,
                                CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("rank").requires((source) -> {
            if(ModConfig.INSTANCE.rankCommandRequiresAdmin && !source.hasPermissionLevel(4)) return false;
            try {
                ServerPlayerEntity player = source.getPlayer();
                if(player == null) return true;
                Rank rank = DatabaseSelector.getPlayerRank(source.getPlayer().getUuid());
                return (rank != null && rank.id >= ModConfig.INSTANCE.minimumRankIdForRankCommand) || source.hasPermissionLevel(4);
            } catch(Exception ignored) {
                return false;
            }
        }).then(CommandManager.literal("set").then(CommandManager.argument("username", EntityArgumentType.player())
                .then(CommandManager.argument("rank", new RankArgumentType()).executes((context) -> {
                    Rank rank = context.getArgument("rank", Rank.class);
                    ServerPlayerEntity dispatchingPlayer = context.getSource().getPlayer();
                    ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "username");
                    Rank playerRank;
                    try {
                        playerRank = DatabaseSelector.getPlayerRank(player.getUuid());
                    } catch(SQLException e) {
                        PoliceTerminal.LOGGER.error("Could not get player rank: ", e);
                        return 0;
                    }
                    if(!ModConfig.INSTANCE.officerCanGrantRankHigherThanHis && dispatchingPlayer != null) {
                        try {
                            Rank dispatchingPlayerRank = DatabaseSelector.getPlayerRank(dispatchingPlayer.getUuid());
                            if(!dispatchingPlayer.hasPermissionLevel(4)) {
                                if(dispatchingPlayerRank == null) return 0;
                                if(rank.id > dispatchingPlayerRank.id) return 0;
                                if(playerRank != null && playerRank.id >= dispatchingPlayerRank.id) return 0;
                            }
                        } catch(Exception ignored) {
                            return 0;
                        }
                    }
                    try {
                        DatabaseUpdater.setPlayerRank(player.getUuid(), rank);
                        if(DatabaseSelector.getPlayerStatus(player.getUuid()) == null) {
                            DatabaseUpdater.setPlayerStatus(player.getUuid(), Status.OUT_OF_SERVICE);
                            SendStatusS2CPacket.send(player, Status.OUT_OF_SERVICE);
                        }
                        SendRankS2CPacket.send(player, rank);
                        context.getSource().getServer().getCommandManager().sendCommandTree(player);
                        if(playerRank == null) playerRank = CIVILLIAN_RANK;
                        MutableText feedback;
                        if(dispatchingPlayer == null)
                            feedback = Text.literal(player.getGameProfile().getName())
                                    .append(playerRank.id > rank.id ? WAS_DEMOTED_TEXT : WAS_PROMOTED_TEXT);
                        else
                            feedback = Text.literal(dispatchingPlayer.getGameProfile().getName())
                                    .append(playerRank.id > rank.id ? DEMOTED_TEXT : PROMOTED_TEXT)
                                    .append(player.getGameProfile().getName());
                        feedback.append(FROM_TEXT).append(Text.literal(playerRank.rank).withColor(playerRank.color))
                                .append(TO_TEXT).append(Text.literal(rank.rank).withColor(rank.color));
                        context.getSource().sendFeedback(() -> feedback, true);
                        if(dispatchingPlayer != player)
                            player.sendMessage(feedback);
                    } catch(SQLException e) {
                        PoliceTerminal.LOGGER.error("Could not set rank for player: ", e);
                    }
                    return 1;
                }))
        )).then(CommandManager.literal("unset").then(CommandManager.argument("username", EntityArgumentType.player())
                .executes((context) -> {
                    ServerPlayerEntity dispatchingPlayer = context.getSource().getPlayer();
                    ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "username");
                    Rank playerRank;
                    try {
                        playerRank = DatabaseSelector.getPlayerRank(player.getUuid());
                    } catch(SQLException e) {
                        PoliceTerminal.LOGGER.error("Could not get player rank: ", e);
                        return 0;
                    }
                    if(!ModConfig.INSTANCE.officerCanGrantRankHigherThanHis && dispatchingPlayer != null) {
                        try {
                            Rank dispatchingPlayerRank = DatabaseSelector.getPlayerRank(dispatchingPlayer.getUuid());
                            if(!dispatchingPlayer.hasPermissionLevel(4)) {
                                if(dispatchingPlayerRank == null) return 0;
                                if(playerRank != null && playerRank.id >= dispatchingPlayerRank.id) return 0;
                            }
                        } catch(Exception ignored) {
                            return 0;
                        }
                    }
                    try {
                        DatabaseUpdater.setPlayerRank(player.getUuid(), null);
                        DatabaseUpdater.setPlayerStatus(player.getUuid(), null);
                        SendStatusS2CPacket.send(player, null);
                        SendRankS2CPacket.send(player, null);
                        context.getSource().getServer().getCommandManager().sendCommandTree(player);
                        if(playerRank == null) playerRank = CIVILLIAN_RANK;
                        MutableText feedback;
                        if(dispatchingPlayer == null)
                            feedback = Text.literal(player.getGameProfile().getName())
                                    .append(WAS_DEMOTED_TEXT);
                        else
                            feedback = Text.literal(dispatchingPlayer.getGameProfile().getName()).append(DEMOTED_TEXT)
                                    .append(player.getGameProfile().getName());
                        feedback.append(FROM_TEXT).append(Text.literal(playerRank.rank).withColor(playerRank.color))
                                .append(TO_TEXT).append(Text.literal(CIVILLIAN_RANK.rank).withColor(CIVILLIAN_RANK.color));
                        context.getSource().sendFeedback(() -> feedback, true);
                        if(dispatchingPlayer != player)
                            player.sendMessage(feedback);
                    } catch(SQLException e) {
                        PoliceTerminal.LOGGER.error("Could not unset rank for player: ", e);
                    }
                    return 1;
                })
        )));
    }
}