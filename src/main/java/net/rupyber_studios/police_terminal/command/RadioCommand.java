package net.rupyber_studios.police_terminal.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.rupyber_studios.police_terminal.PoliceTerminal;
import net.rupyber_studios.police_terminal.command.argument.OnlineCallsignArgumentType;
import net.rupyber_studios.police_terminal.database.DatabaseSelector;
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
        dispatcher.register(CommandManager.literal("radio").requires((source) -> {
            try {
                if(source.getPlayer() == null) return false;
                return DatabaseSelector.getPlayerCallsign(source.getPlayer().getUuid()) != null;
            } catch(SQLException e) {
                return false;
            }
        }).then(CommandManager.literal("like").then(CommandManager.argument("callsign", StringArgumentType.string())
                .then(CommandManager.argument("message", MessageArgumentType.message())
                        .executes((context) -> {
                            ServerPlayerEntity dispatchingPlayer = context.getSource().getPlayer();
                            if(dispatchingPlayer == null) return 0;
                            String callsign = context.getArgument("callsign", String.class);
                            Text message = MessageArgumentType.getMessage(context, "message");
                            try {
                                String dispatchingPlayerCallsign = DatabaseSelector.getPlayerCallsign(dispatchingPlayer.getUuid());
                                List<UUID> playerUuids = DatabaseSelector.getPlayerUuidsFromCallsignLike(callsign);
                                if(playerUuids == null) return 0;
                                Text feedback = RADIO_TEXT.copy().append("(§9" + dispatchingPlayerCallsign + "§r")
                                        .append(TO_TEXT).append("§9" + callsign + "§r):\n")
                                        .append(message);
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
                        })
                )
        )).then(CommandManager.argument("callsign", new OnlineCallsignArgumentType())
                .then(CommandManager.argument("message", MessageArgumentType.message())
                        .executes((context) -> {
                            ServerPlayerEntity dispatchingPlayer = context.getSource().getPlayer();
                            if(dispatchingPlayer == null) return 0;
                            String callsign = context.getArgument("callsign", String.class);
                            Text message = MessageArgumentType.getMessage(context, "message");
                            try {
                                String dispatchingPlayerCallsign = DatabaseSelector.getPlayerCallsign(dispatchingPlayer.getUuid());
                                UUID playerUuid = DatabaseSelector.getPlayerUuidFromCallsign(callsign);
                                ServerPlayerEntity player = context.getSource().getServer().getPlayerManager().getPlayer(playerUuid);
                                if(player == null) return 1;
                                Text feedback = RADIO_TEXT.copy().append("(§9" + dispatchingPlayerCallsign + "§r")
                                        .append(TO_TEXT).append("§9" + callsign + "§r):\n")
                                        .append(message);
                                context.getSource().sendFeedback(() -> feedback, false);
                                if(dispatchingPlayer != player)
                                    player.sendMessage(feedback);
                            } catch(SQLException e) {
                                PoliceTerminal.LOGGER.error("Could not get player from callsign: ", e);
                            }
                            return 1;
                        })
                )
        ));
    }
}