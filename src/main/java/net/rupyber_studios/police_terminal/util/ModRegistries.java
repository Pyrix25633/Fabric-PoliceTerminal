package net.rupyber_studios.police_terminal.util;

import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.util.Identifier;
import net.rupyber_studios.police_terminal.PoliceTerminal;
import net.rupyber_studios.police_terminal.command.*;
import net.rupyber_studios.police_terminal.command.argument.OnlineCallsignArgumentType;
import net.rupyber_studios.police_terminal.command.argument.RankArgumentType;
import net.rupyber_studios.police_terminal.command.argument.StatusArgumentType;
import net.rupyber_studios.police_terminal.command.argument.UnusedCallsignArgumentType;

public class ModRegistries {
    public static void registerCommands() {
        ArgumentTypeRegistry.registerArgumentType(new Identifier(PoliceTerminal.MOD_ID, "status"),
                StatusArgumentType.class, ConstantArgumentSerializer.of(StatusArgumentType::new));
        ArgumentTypeRegistry.registerArgumentType(new Identifier(PoliceTerminal.MOD_ID, "rank"),
                RankArgumentType.class, ConstantArgumentSerializer.of(RankArgumentType::new));
        ArgumentTypeRegistry.registerArgumentType(new Identifier(PoliceTerminal.MOD_ID, "unused_callsign"),
                UnusedCallsignArgumentType.class, ConstantArgumentSerializer.of(UnusedCallsignArgumentType::new));
        ArgumentTypeRegistry.registerArgumentType(new Identifier(PoliceTerminal.MOD_ID, "online_callsign"),
                OnlineCallsignArgumentType.class, ConstantArgumentSerializer.of(OnlineCallsignArgumentType::new));
        CommandRegistrationCallback.EVENT.register(StatusCommand::register);
        CommandRegistrationCallback.EVENT.register(RankCommand::register);
        CommandRegistrationCallback.EVENT.register(OfficerCommand::register);
        CommandRegistrationCallback.EVENT.register(CallsignCommand::register);
        CommandRegistrationCallback.EVENT.register(RadioCommand::register);
        CommandRegistrationCallback.EVENT.register(TerminalCommand::register);
        CommandRegistrationCallback.EVENT.register(EmergencyCommand::register);
    }
}