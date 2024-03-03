package net.rupyber_studios.police_terminal.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.rupyber_studios.police_terminal.PoliceTerminal;
import net.rupyber_studios.police_terminal.command.argument.OnlineCallsignArgumentType;
import net.rupyber_studios.rupyber_database_api.table.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class RadioCommand {
    private static final Text RADIO_TEXT = Text.translatable("commands.radio.success.radio");
    private static final Text TO_TEXT = Text.translatable("commands.radio.success.to");

    public static void register(@NotNull CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess registryAccess,
                                CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("radio")
                .requires(RadioCommand::canExecute)
                .then(CommandManager.literal("like")
                        .then(CommandManager.argument("callsign", StringArgumentType.string())
                                .then(CommandManager.argument("message", MessageArgumentType.message())
                                        .executes(RadioCommand::executeRadioLike))))
                .then(CommandManager.argument("callsign", new OnlineCallsignArgumentType())
                        .then(CommandManager.argument("message", MessageArgumentType.message())
                                .executes(RadioCommand::executeRadio)))
        );
    }

    private static boolean canExecute(@NotNull ServerCommandSource source) {
        try {
            if(source.getPlayer() == null) return false;
            return Player.selectCallsignFromUuid(source.getPlayer().getUuid()) != null;
        } catch(SQLException e) {
            return false;
        }
    }

    private static int executeRadioLike(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        return execute(context, true);
    }

    private static int executeRadio(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        return execute(context, false);
    }

    private static int execute(@NotNull CommandContext<ServerCommandSource> context, boolean like)
            throws CommandSyntaxException {
        ServerPlayerEntity dispatchingPlayer = context.getSource().getPlayer();
        if(dispatchingPlayer == null) return 0;
        String callsign = context.getArgument("callsign", String.class);
        Text message = MessageArgumentType.getMessage(context, "message");
        try {
            String dispatchingPlayerCallsign = Player.selectCallsignFromUuid(dispatchingPlayer.getUuid());
            List<UUID> playerUuids = getPlayerUuids(callsign, like);
            if(playerUuids == null) return 0;
            Text feedback = buildFeedback(dispatchingPlayerCallsign, callsign, message);
            context.getSource().sendFeedback(() -> feedback, false);
            for(UUID playerUuid : playerUuids) {
                ServerPlayerEntity player = context.getSource().getServer().getPlayerManager().getPlayer(playerUuid);
                if(player != null && dispatchingPlayer != player)
                    player.sendMessage(feedback);
            }
        } catch(SQLException e) {
            PoliceTerminal.LOGGER.error("Could not get player from callsign: ", e);
        }
        return 1;
    }

    private static List<UUID> getPlayerUuids(String callsign, boolean like) throws SQLException {
        List<UUID> playerUuids;
        if(like)
            playerUuids = Player.selectUuidsFromCallsignLike(callsign);
        else {
            UUID playerUuid = Player.selectUuidFromCallsign(callsign);
            if(playerUuid != null) playerUuids = List.of(playerUuid);
            else playerUuids = null;
        }
        return playerUuids;
    }

    private static Text buildFeedback(String dispatchingPlayerCallsign, String callsign, Text message) {
        return RADIO_TEXT.copy().append("§3(§9" + dispatchingPlayerCallsign)
                .append(TO_TEXT).append("§9" + callsign + "§3):§r ")
                .append(message);
    }
}