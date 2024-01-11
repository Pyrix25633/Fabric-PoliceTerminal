package net.rupyber_studios.police_terminal.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.rupyber_studios.police_terminal.config.ModConfig;
import net.rupyber_studios.police_terminal.util.PlayerInfo;
import net.rupyber_studios.police_terminal.util.Rank;
import org.jetbrains.annotations.NotNull;

public class RankCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess registryAccess,
                                CommandManager.RegistrationEnvironment environment) {
        // TODO: modify
        for(Rank rank : ModConfig.INSTANCE.ranks) {
            registerStatusCommand(dispatcher, rank);
        }
    }

    public static void registerStatusCommand(@NotNull CommandDispatcher<ServerCommandSource> dispatcher, @NotNull Rank rank) {
        // TODO: fix, needs argument, not literal
        dispatcher.register(CommandManager.literal("rank").then(CommandManager.argument("username", EntityArgumentType.players())
                .then(CommandManager.literal(rank.rank.toLowerCase().replaceAll(" ", "_")))
                .executes((context) -> {
                    // TODO handle with packets
                    PlayerInfo.info.rank = rank;
                    return 1;
                })
        ));
    }
}