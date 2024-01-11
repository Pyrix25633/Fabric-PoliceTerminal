package net.rupyber_studios.police_terminal.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.rupyber_studios.police_terminal.PoliceTerminal;
import net.rupyber_studios.police_terminal.command.argument.StatusArgumentType;
import net.rupyber_studios.police_terminal.util.Status;
import org.jetbrains.annotations.NotNull;

public class StatusCommand {
    public static void register(@NotNull CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess registryAccess,
                                CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("status").then(CommandManager.argument("status", new StatusArgumentType())
                .executes((context -> {
                    Status status = context.getArgument("status", Status.class);
                    //TODO: make it work
                    PoliceTerminal.LOGGER.warn(status.name());
                    return 1;
        }))));
    }
}