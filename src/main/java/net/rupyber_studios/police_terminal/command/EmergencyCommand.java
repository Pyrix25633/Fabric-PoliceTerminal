package net.rupyber_studios.police_terminal.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.rupyber_studios.police_terminal.PoliceTerminal;
import net.rupyber_studios.rupyber_database_api.table.EmergencyCall;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public class EmergencyCommand {
    private static final Text TO_911_TEXT = Text.translatable("commands.emergency.success.to_911");

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
            String description = StringArgumentType.getString(context, "description");
            EmergencyCall.insert(player.getUuid(), player.getPos(), description);
            Text feedback = Text.literal(player.getGameProfile().getName()).append(TO_911_TEXT).append(description);
            context.getSource().sendFeedback(() -> feedback, false);
        } catch(SQLException e) {
            PoliceTerminal.LOGGER.error("Could not create emergency call: ", e);
            return 0;
        }
        return 1;
    }
}