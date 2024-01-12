package net.rupyber_studios.police_terminal.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.rupyber_studios.police_terminal.config.ModConfig;
import net.rupyber_studios.police_terminal.database.DatabaseManager;
import net.rupyber_studios.police_terminal.util.PlayerInfo;
import net.rupyber_studios.police_terminal.util.Rank;
import org.jetbrains.annotations.NotNull;

public class RankCommand {
    public static void register(@NotNull CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess registryAccess,
                                CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("rank").requires((source) -> {
            if(ModConfig.INSTANCE.rankCommandRequiresAdmin && !source.hasPermissionLevel(4)) return false;
            try {
                ServerPlayerEntity player = source.getPlayer();
                if(player == null) return false;
                Rank rank = DatabaseManager.getPlayerRank(source.getPlayer().getUuid());
                return rank != null && rank.id >= ModConfig.INSTANCE.minimumRankIdForRankCommand;
            } catch(Exception ignored) {
                return false;
            }
        }).then(CommandManager.argument("username", EntityArgumentType.players())
                /*.then(CommandManager.argument("rank", ))
                .executes((context) -> {
                    // TODO handle with packets
                    PlayerInfo.info.rank = rank;
                    return 1;
                })*/
        ));
    }
}