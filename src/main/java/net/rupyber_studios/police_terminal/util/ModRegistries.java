package net.rupyber_studios.police_terminal.util;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.rupyber_studios.police_terminal.command.StatusCommand;

public class ModRegistries {
    public static void registerCommands() {
        CommandRegistrationCallback.EVENT.register(StatusCommand::register);
    }
}