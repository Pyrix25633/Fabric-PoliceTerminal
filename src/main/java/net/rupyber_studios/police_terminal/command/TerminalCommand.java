package net.rupyber_studios.police_terminal.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.rupyber_studios.rupyber_database_api.util.Officer;
import net.rupyber_studios.rupyber_database_api.util.PlayerInfo;
import org.jetbrains.annotations.NotNull;

public class TerminalCommand {
    private static final Text TERMINAL_CREDENTIALS_TEXT = Text.translatable("commands.terminal.success.terminal_credentials");
    private static final Text CALLSIGN_TEXT = Text.translatable("commands.terminal.success.callsign");
    private static final Text PASSWORD_TEXT = Text.translatable("commands.terminal.success.password");

    public static void register(@NotNull CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess registryAccess,
                                CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("terminal")
                .then(CommandManager.literal("credentials")
                        .requires(TerminalCommand::canExecute)
                        .executes(TerminalCommand::execute))
        );
    }

    private static boolean canExecute(@NotNull ServerCommandSource source) {
        ServerPlayerEntity player = source.getPlayer();
        if(player == null) return false;
        PlayerInfo info = Officer.selectPlayerInfoWhereUuid(player.getUuid());
        return info.rank != null && info.callsign != null;
    }

    private static int execute(@NotNull CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if(player == null) return 0;
        String callsign = Officer.selectCallsignWhereUuid(player.getUuid());
        String password = Officer.initPasswordWhereUuid(player.getUuid());
        Text feedback = TERMINAL_CREDENTIALS_TEXT.copy().append("\n")
                .append(CALLSIGN_TEXT).append("§9" + callsign + "§r\n")
                .append(PASSWORD_TEXT).append("§5" + password);
        context.getSource().sendFeedback(() -> feedback, false);
        return 1;
    }
}