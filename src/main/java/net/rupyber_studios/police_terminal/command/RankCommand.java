package net.rupyber_studios.police_terminal.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
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
import net.rupyber_studios.police_terminal.networking.packet.SendRankS2CPacket;
import net.rupyber_studios.police_terminal.networking.packet.SendStatusS2CPacket;
import net.rupyber_studios.rupyber_database_api.table.Player;
import net.rupyber_studios.rupyber_database_api.table.Rank;
import net.rupyber_studios.rupyber_database_api.util.Status;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public class RankCommand {
    private static final Text WAS_PROMOTED_TEXT = Text.translatable("commands.rank.success.was_promoted");
    private static final Text WAS_DEMOTED_TEXT = Text.translatable("commands.rank.success.was_demoted");
    private static final Text PROMOTED_TEXT = Text.translatable("commands.rank.success.promoted");
    private static final Text DEMOTED_TEXT = Text.translatable("commands.rank.success.demoted");
    private static final Text FROM_TEXT = Text.translatable("commands.rank.success.from");
    private static final Text TO_TEXT = Text.translatable("commands.rank.success.to");
    private static final Rank CIVILLIAN_RANK = new Rank(0, "Civilian", false, 0xAAAAAA);

    public static void register(@NotNull CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess registryAccess,
                                CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("rank").requires(RankCommand::canExecute)
                .then(CommandManager.literal("set")
                        .then(CommandManager.argument("username", EntityArgumentType.player())
                                .then(CommandManager.argument("rank", new RankArgumentType())
                                        .executes(RankCommand::executeRankSet))))
                .then(CommandManager.literal("unset")
                        .then(CommandManager.argument("username", EntityArgumentType.player())
                                .executes(RankCommand::executeRankUnset)))
        );
    }

    private static boolean canExecute(@NotNull ServerCommandSource source) {
        if(ModConfig.INSTANCE.rankCommandRequiresAdmin && !source.hasPermissionLevel(4)) return false;
        try {
            ServerPlayerEntity player = source.getPlayer();
            if(player == null) return true;
            Rank rank = Player.selectRankFromUuid(source.getPlayer().getUuid());
            return (rank != null && rank.id >= ModConfig.INSTANCE.minimumRankIdForRankCommand) ||
                    source.hasPermissionLevel(4);
        } catch(Exception ignored) {
            return false;
        }
    }

    private static int executeRankSet(@NotNull CommandContext<ServerCommandSource> context)
            throws CommandSyntaxException {
        return execute(context, context.getArgument("rank", Rank.class));
    }

    private static int executeRankUnset(@NotNull CommandContext<ServerCommandSource> context)
            throws CommandSyntaxException {
        return execute(context, null);
    }

    private static int execute(@NotNull CommandContext<ServerCommandSource> context, Rank rank)
            throws CommandSyntaxException {
        try {
            ServerPlayerEntity dispatchingPlayer = context.getSource().getPlayer();
            ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "username");
            Rank playerRank = selectPlayerRank(player);
            if(!ModConfig.INSTANCE.officerCanGrantRankHigherThanHis && dispatchingPlayer != null) {
                if(!canDispatchingPlayerModifyPlayerRank(dispatchingPlayer, rank, playerRank)) return 0;
            }
            updatePlayerRank(player, rank);
            context.getSource().getServer().getCommandManager().sendCommandTree(player);
            Text feedback = buildFeedback(dispatchingPlayer, player, rank, playerRank);
            context.getSource().sendFeedback(() -> feedback, true);
            if(dispatchingPlayer != player)
                player.sendMessage(feedback);
        } catch(Exception e) {
            if(e instanceof CommandSyntaxException cse) throw cse;
            return 0;
        }
        return 1;
    }

    private static Rank selectPlayerRank(@NotNull ServerPlayerEntity player) throws SQLException {
        try {
            return Player.selectRankFromUuid(player.getUuid());
        } catch(SQLException e) {
            PoliceTerminal.LOGGER.error("Could not get player rank: ", e);
            throw e;
        }
    }

    private static boolean canDispatchingPlayerModifyPlayerRank(ServerPlayerEntity dispatchingPlayer,
                                                                Rank rank, Rank playerRank) throws SQLException {
        Rank dispatchingPlayerRank = selectPlayerRank(dispatchingPlayer);
        if(!dispatchingPlayer.hasPermissionLevel(4)) {
            if(dispatchingPlayerRank == null) return false;
            if(rank != null && rank.id > dispatchingPlayerRank.id) return false;
            return playerRank == null || playerRank.id < dispatchingPlayerRank.id;
        }
        return true;
    }

    private static void updatePlayerRank(@NotNull ServerPlayerEntity player, Rank rank) throws SQLException {
        try {
            Player.updateRankFromUuid(player.getUuid(), rank);
            if(rank != null) {
                if(Player.selectStatusFromUuid(player.getUuid()) == null) {
                    Player.updateStatusFromUuid(player.getUuid(), Status.OUT_OF_SERVICE);
                    SendStatusS2CPacket.send(player, Status.OUT_OF_SERVICE);
                }
            }
            else
                SendStatusS2CPacket.send(player, null);
            SendRankS2CPacket.send(player, rank);
        } catch(SQLException e) {
            PoliceTerminal.LOGGER.error("Could not " + (rank == null ? "un" : "") + "set rank for player: ", e);
            throw e;
        }
    }

    private static @NotNull Text buildFeedback(ServerPlayerEntity dispatchingPlayer, ServerPlayerEntity player,
                                               Rank rank, Rank playerRank) {
        if(rank == null) rank = CIVILLIAN_RANK;
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
        return feedback;
    }
}