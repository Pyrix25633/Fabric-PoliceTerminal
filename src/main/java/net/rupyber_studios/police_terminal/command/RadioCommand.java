package net.rupyber_studios.police_terminal.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.rupyber_studios.police_terminal.PoliceTerminal;
import net.rupyber_studios.police_terminal.command.argument.OnlineCallsignArgumentType;
import net.rupyber_studios.police_terminal.database.DatabaseManager;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.UUID;

public class RadioCommand {
    private static final Text RADIO_TEXT = Text.translatable("commands.radio.success.radio");

    public static void register(@NotNull CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess registryAccess,
                                CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("radio").requires((source) -> {
            try {
                if(source.getPlayer() == null) return false;
                return DatabaseManager.getPlayerCallsign(source.getPlayer().getUuid()) != null;
            } catch(SQLException e) {
                return false;
            }
        }).then(CommandManager.argument("callsign", new OnlineCallsignArgumentType())
                .then(CommandManager.argument("message", StringArgumentType.string())
                        .executes((context) -> {
                            ServerPlayerEntity dispatchingPlayer = context.getSource().getPlayer();
                            if(dispatchingPlayer == null) return 0;
                            String callsign = context.getArgument("callsign", String.class);
                            String message = StringArgumentType.getString(context, "message");
                            try {
                                String dispatchingPlayerCallsign = DatabaseManager.getPlayerCallsign(dispatchingPlayer.getUuid());
                                UUID playerUuid = DatabaseManager.getPlayerUuidFromCallsign(callsign);
                                ServerPlayerEntity player = context.getSource().getServer().getPlayerManager().getPlayer(playerUuid);
                                if(player == null) return 0;
                                Text feedback = RADIO_TEXT.copy().append("(§9" + dispatchingPlayerCallsign + "§r):\n")
                                        .append(message);
                                context.getSource().sendFeedback(() -> feedback, false);
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