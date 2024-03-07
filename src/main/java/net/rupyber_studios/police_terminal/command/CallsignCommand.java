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
import net.rupyber_studios.police_terminal.command.argument.OnlineCallsignArgumentType;
import net.rupyber_studios.police_terminal.command.argument.UnusedCallsignArgumentType;
import net.rupyber_studios.police_terminal.config.ModConfig;
import net.rupyber_studios.police_terminal.networking.packet.SendCallsignS2CPacket;
import net.rupyber_studios.rupyber_database_api.table.Rank;
import net.rupyber_studios.rupyber_database_api.util.Callsign;
import net.rupyber_studios.rupyber_database_api.util.Officer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.util.UUID;

public class CallsignCommand {
    private static final Text YOUR_CALLSIGN_IS_NOW_TEXT = Text.translatable("commands.callsign.success.your_callsign_is_now");
    private static final Text CALLSIGN_TEXT = Text.translatable("commands.callsign.success.callsign");
    private static final Text WAS_RESERVED_FOR_TEXT = Text.translatable("commands.callsign.success.was_reserved_for");
    private static final Text RESERVED_CALLSIGN_TEXT = Text.translatable("commands.callsign.success.reserved_callsign");
    private static final Text FOR_TEXT = Text.translatable("commands.callsign.success.for");
    private static final Text RELEASED_CALLSIGN_TEXT = Text.translatable("commands.callsign.success.released_callsign");
    private static final Text WAS_RELEASED_TEXT = Text.translatable("commands.callsign.success.was_released");

    public static void register(@NotNull CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess registryAccess,
                                CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("callsign")
                .then(CommandManager.literal("request")
                        .requires(CallsignCommand::canExecute).executes(CallsignCommand::executeCallsignRequest)
                        .then(CommandManager.argument("callsign", new UnusedCallsignArgumentType())
                                .executes(CallsignCommand::executeCallsignRequestWithArgument)))
                .then(CommandManager.literal("reserve")
                        .requires(CallsignCommand::canExecuteStrict)
                        .then(CommandManager.argument("username", EntityArgumentType.player())
                                .then(CommandManager.argument("callsign", new UnusedCallsignArgumentType())
                                        .executes(CallsignCommand::executeCallsignReserve))))
                .then(CommandManager.literal("release")
                        .requires(CallsignCommand::canExecute)
                        .then(CommandManager.argument("callsign", new OnlineCallsignArgumentType())
                                .executes(CallsignCommand::executeCallsignRelease)))
        );
    }

    private static boolean canExecute(ServerCommandSource source) {
        if(ModConfig.INSTANCE.callsignCommandRequiresAdmin && !source.hasPermissionLevel(4)) return false;
        try {
            return getPlayerRankFromSource(source) != null;
        } catch(Exception ignored) {
            return false;
        }
    }

    private static boolean canExecuteStrict(ServerCommandSource source) {
        if(ModConfig.INSTANCE.callsignCommandRequiresAdmin && !source.hasPermissionLevel(4)) return false;
        try {
            Rank rank = getPlayerRankFromSource(source);
            return (rank != null && rank.id >= ModConfig.INSTANCE.minimumRankIdForCallsignCommand) || source.hasPermissionLevel(4);
        } catch(Exception ignored) {
            return false;
        }
    }

    private static @Nullable Rank getPlayerRankFromSource(@NotNull ServerCommandSource source) throws SQLException {
        ServerPlayerEntity player = source.getPlayer();
        if(player == null) return null;
        return Officer.selectRankFromUuid(source.getPlayer().getUuid());
    }

    private static int executeCallsignRequest(CommandContext<ServerCommandSource> context)
            throws CommandSyntaxException {
        try {
            return execute(context, Callsign.createUnusedCallsign());
        } catch (SQLException e) {
            PoliceTerminal.LOGGER.info("Unable to create unused callsign: ", e);
            return 0;
        }
    }

    private static int executeCallsignRequestWithArgument(CommandContext<ServerCommandSource> context)
            throws CommandSyntaxException {
        return execute(context, context.getArgument("callsign", String.class));
    }

    private static int execute(@NotNull CommandContext<ServerCommandSource> context, String callsign) {
        try {
            ServerPlayerEntity player = context.getSource().getPlayer();
            if(player == null) return 0;
            Officer.updateCallsignFromUuid(player.getUuid(), callsign, false);
            OnlineCallsignArgumentType.init();
            SendCallsignS2CPacket.send(player, callsign);
            context.getSource().getServer().getCommandManager().sendCommandTree(player);
            Text feedback = YOUR_CALLSIGN_IS_NOW_TEXT.copy().append("§9" + callsign);
            context.getSource().sendFeedback(() -> feedback, false);
        } catch(SQLException e) {
            PoliceTerminal.LOGGER.error("Could not assign a callsign to player: ", e);
            return 0;
        }
        return 1;
    }

    private static int executeCallsignReserve(@NotNull CommandContext<ServerCommandSource> context)
            throws CommandSyntaxException {
        String callsign = context.getArgument("callsign", String.class);
        ServerPlayerEntity dispatchingPlayer = context.getSource().getPlayer();
        ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "username");
        try {
            Officer.updateCallsignFromUuid(player.getUuid(), callsign, true);
            SendCallsignS2CPacket.send(player, callsign);
            OnlineCallsignArgumentType.init();
            context.getSource().getServer().getCommandManager().sendCommandTree(player);
            Text feedback = buildCallsignReserveFeedback(dispatchingPlayer, player, callsign);
            context.getSource().sendFeedback(() -> feedback, true);
            if(dispatchingPlayer != player)
                player.sendMessage(feedback);
        } catch(SQLException e) {
            PoliceTerminal.LOGGER.error("Could not reserve callsign for player: ", e);
            return 0;
        }
        return 1;
    }

    private static @NotNull Text buildCallsignReserveFeedback(ServerPlayerEntity dispatchingPlayer,
                                                              ServerPlayerEntity player, String callsign) {
        MutableText feedback;
        if(dispatchingPlayer == null)
            feedback = CALLSIGN_TEXT.copy().append("§9" + callsign + "§r")
                    .append(WAS_RESERVED_FOR_TEXT);
        else
            feedback = Text.literal(dispatchingPlayer.getGameProfile().getName())
                    .append(RESERVED_CALLSIGN_TEXT).append("§9" + callsign + "§r").append(FOR_TEXT);
        feedback.append(player.getGameProfile().getName());
        return feedback;
    }

    private static int executeCallsignRelease(@NotNull CommandContext<ServerCommandSource> context)
            throws CommandSyntaxException {
        String callsign = context.getArgument("callsign", String.class);
        ServerPlayerEntity dispatchingPlayer = context.getSource().getPlayer();
        try {
            UUID playerUuid = Officer.selectUuidFromCallsign(callsign);
            if(playerUuid == null) return 0;
            ServerPlayerEntity player = context.getSource().getServer().getPlayerManager().getPlayer(playerUuid);
            if(player == null) return 0;
            Officer.updateCallsignFromUuid(playerUuid, null, false);
            SendCallsignS2CPacket.send(player, callsign);
            OnlineCallsignArgumentType.init();
            context.getSource().getServer().getCommandManager().sendCommandTree(player);
            Text feedback = buildCallsignReleaseFeedback(dispatchingPlayer, player, callsign);
            context.getSource().sendFeedback(() -> feedback, true);
            if(dispatchingPlayer != player)
                player.sendMessage(feedback);
        } catch(SQLException e) {
            PoliceTerminal.LOGGER.error("Could not release callsign: ", e);
            return 0;
        }
        return 1;
    }

    private static Text buildCallsignReleaseFeedback(ServerPlayerEntity dispatchingPlayer, ServerPlayerEntity player,
                                                     String callsign) {
        Text feedback;
        if(dispatchingPlayer == null)
            feedback = CALLSIGN_TEXT.copy().append("§9" + callsign + "§r")
                    .append(" (" + player.getGameProfile().getName() + ")").append(WAS_RELEASED_TEXT);
        else
            feedback = Text.literal(dispatchingPlayer.getGameProfile().getName())
                    .append(RELEASED_CALLSIGN_TEXT).append("§9" + callsign + "§r")
                    .append(" (" + player.getGameProfile().getName() + ")");
        return feedback;
    }
}