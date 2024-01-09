package net.rupyber_studios.police_terminal.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.rupyber_studios.police_terminal.client.HudOverlay;
import net.rupyber_studios.police_terminal.util.Status;
import org.jetbrains.annotations.NotNull;

public class StatusCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess registryAccess,
                                CommandManager.RegistrationEnvironment environment) {
        for(Status status : Status.values()) {
            registerStatusCommand(dispatcher, status);
        }
    }

    public static void registerStatusCommand(@NotNull CommandDispatcher<ServerCommandSource> dispatcher, @NotNull Status status) {
        dispatcher.register(CommandManager.literal("status").then(CommandManager.literal(status.name().toLowerCase())
                .executes((context) -> {
                    // TODO handle with packets
                    HudOverlay.status = status;
                    return 1;
                })
        ));
    }
}