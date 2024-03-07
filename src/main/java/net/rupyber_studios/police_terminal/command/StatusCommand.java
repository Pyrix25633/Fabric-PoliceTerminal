package net.rupyber_studios.police_terminal.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.rupyber_studios.police_terminal.PoliceTerminal;
import net.rupyber_studios.police_terminal.command.argument.StatusArgumentType;
import net.rupyber_studios.police_terminal.networking.packet.SendStatusS2CPacket;
import net.rupyber_studios.rupyber_database_api.util.Officer;
import net.rupyber_studios.rupyber_database_api.util.Status;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public class StatusCommand {
    private static final Text STATUS_SET_TO_TEXT = Text.translatable("commands.status.success.status_set_to");

    public static void register(@NotNull CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess registryAccess,
                                CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("status")
                .requires(StatusCommand::canExecute)
                .then(CommandManager.argument("status", new StatusArgumentType())
                        .executes(StatusCommand::execute))
        );
    }

    private static boolean canExecute(@NotNull ServerCommandSource source) {
        ServerPlayerEntity player = source.getPlayer();
        if(player == null) return false;
        try {
            return Officer.selectRankFromUuid(player.getUuid()) != null;
        } catch(Exception ignored) {
            return false;
        }
    }

    private static int execute(@NotNull CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Status status = context.getArgument("status", Status.class);
        ServerPlayerEntity player = context.getSource().getPlayer();
        if(player == null) return 0;
        try {
            Officer.updateStatusFromUuid(player.getUuid(), status);
            SendStatusS2CPacket.send(player, status);
            MutableText feedback = STATUS_SET_TO_TEXT.copy().append(status.getText());
            context.getSource().sendFeedback(() -> feedback, false);
        } catch(SQLException e) {
            PoliceTerminal.LOGGER.error("Could not set status for player: ", e);
        }
        return 1;
    }
}