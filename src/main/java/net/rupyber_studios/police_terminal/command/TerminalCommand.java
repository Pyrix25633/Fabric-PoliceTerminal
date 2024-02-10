package net.rupyber_studios.police_terminal.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.rupyber_studios.police_terminal.PoliceTerminal;
import net.rupyber_studios.police_terminal.database.DatabaseSelector;
import net.rupyber_studios.police_terminal.database.DatabaseUpdater;
import net.rupyber_studios.police_terminal.util.PlayerInfo;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public class TerminalCommand {
    private static final Text TERMINAL_CREDENTIALS_TEXT = Text.translatable("commands.terminal.success.terminal_credentials");
    private static final Text CALLSIGN_TEXT = Text.translatable("commands.terminal.success.callsign");
    private static final Text PASSWORD_TEXT = Text.translatable("commands.terminal.success.password");

    public static void register(@NotNull CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess registryAccess,
                                CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("terminal").then(CommandManager.literal("credentials").requires((source) -> {
            ServerPlayerEntity player = source.getPlayer();
            if(player == null) return false;
            try {
                PlayerInfo info = DatabaseSelector.getPlayerInfo(player.getUuid());
                return info.rank != null && info.callsign != null;
            } catch(SQLException e) {
                return false;
            }
        }).executes((context) -> {
            ServerPlayerEntity player = context.getSource().getPlayer();
            if(player == null) return 0;
            try {
                String callsign = DatabaseSelector.getPlayerCallsign(player.getUuid());
                String password = DatabaseUpdater.initPlayerPassword(player.getUuid());
                Text feedback = TERMINAL_CREDENTIALS_TEXT.copy().append("\n")
                        .append(CALLSIGN_TEXT).append("§9" + callsign + "§r\n")
                        .append(PASSWORD_TEXT).append("§5" + password);
                context.getSource().sendFeedback(() -> feedback, false);
            } catch(SQLException e) {
                PoliceTerminal.LOGGER.error("Could not get credentials for player: ", e);
                return 0;
            }
            return 1;
        })));
    }
}