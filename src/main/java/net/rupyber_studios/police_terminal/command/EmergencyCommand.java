package net.rupyber_studios.police_terminal.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.rupyber_studios.police_terminal.PoliceTerminal;
import net.rupyber_studios.police_terminal.database.DatabaseUpdater;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public class EmergencyCommand {
    public static void register(@NotNull CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess registryAccess,
                                CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("emergency").requires(EmergencyCommand::canExecute)
                .then(CommandManager.argument("description", StringArgumentType.greedyString())
                        .executes(EmergencyCommand::execute)
                )
        );
        dispatcher.register(CommandManager.literal("911").requires(EmergencyCommand::canExecute)
                .then(CommandManager.argument("description", StringArgumentType.greedyString())
                        .executes(EmergencyCommand::execute)
                )
        );
    }

    private static boolean canExecute(@NotNull ServerCommandSource source) {
            ServerPlayerEntity player = source.getPlayer();
            return player != null;
    }

    private static int execute(@NotNull CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if(player == null) return 0;
        try {
            DatabaseUpdater.createEmergencyCall(player.getUuid(), player.getPos(),
                    StringArgumentType.getString(context, "description"));
        } catch(SQLException e) {
            PoliceTerminal.LOGGER.error("Could not create emergency call: ", e);
        }
        return 1;
    }
}