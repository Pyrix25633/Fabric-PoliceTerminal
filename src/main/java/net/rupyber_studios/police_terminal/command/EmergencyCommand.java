package net.rupyber_studios.police_terminal.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.rupyber_studios.police_terminal.PoliceTerminal;
import net.rupyber_studios.rupyber_database_api.table.EmergencyCall;
import net.rupyber_studios.rupyber_database_api.util.Officer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.util.UUID;

public class EmergencyCommand {
    private static final Text INCOMING_CALL_TEXT = Text.translatable("commands.emergency.success.incoming_call");
    private static final Text _911_TEXT = Text.translatable("commands.emergency.success.911");
    private static final Text TO_TEXT = Text.translatable("commands.emergency.success.to");
    private static final Text WHATS_YOUR_EMERGENCY_TEXT =
            Text.translatable("commands.emergency.success.whats_your_emergency");
    private static final Text PLEASE_KEEP_CALM_TEXT =
            Text.translatable("commands.emergency.success.please_keep_calm");
    private static final Text NO_EMERGENCY_OPERATORS_AVAILABLE_TEXT =
            Text.translatable("commands.emergency.success.no_emergency_responders_available");

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
            ServerPlayerEntity respondingOfficer = selectRespondingOfficer(context.getSource().getServer());
            Text feedback = buildFeedback(player, respondingOfficer, description);
            if(respondingOfficer != null) {
                int callNumber = EmergencyCall.insertAndReturnCallNumber(player.getUuid(), respondingOfficer.getUuid(),
                        player.getPos(), description);
                respondingOfficer.sendMessage(INCOMING_CALL_TEXT.copy().append(callNumber + "\n").append(feedback));
            }
            if(player != respondingOfficer)
                context.getSource().sendFeedback(() -> feedback, false);
        } catch(SQLException e) {
            PoliceTerminal.LOGGER.error("Could not create emergency call: ", e);
            return 0;
        }
        return 1;
    }

    private static @Nullable ServerPlayerEntity selectRespondingOfficer(@NotNull MinecraftServer server)
            throws SQLException {
        ServerPlayerEntity emergencyOperator;
        do {
            UUID emergencyOperatorUuid = Officer.selectAvailableEmergencyOperator();
            if(emergencyOperatorUuid == null) return null;
            emergencyOperator = server.getPlayerManager().getPlayer(emergencyOperatorUuid);
        } while (emergencyOperator == null);
        return emergencyOperator;
    }

    private static Text buildFeedback(@NotNull ServerPlayerEntity player,
                                      @Nullable ServerPlayerEntity respondingOfficer, String description) {
        String playerUsername = player.getGameProfile().getName();
        if(respondingOfficer == null) {
            return _911_TEXT.copy().append("§1(§4BOT").append(TO_TEXT).append("§r" + playerUsername + "§1): ")
                    .append(NO_EMERGENCY_OPERATORS_AVAILABLE_TEXT);
        }
        else {
            String respondingOfficerUsername = respondingOfficer.getGameProfile().getName();
            return _911_TEXT.copy().append("§c(§r" + respondingOfficerUsername).append(TO_TEXT)
                    .append("§r" + playerUsername + "§c):§r ").append(WHATS_YOUR_EMERGENCY_TEXT).append("\n")
                    .append(_911_TEXT).append("§c(§r" + playerUsername).append(TO_TEXT)
                    .append("§r" + respondingOfficerUsername + "§c):§r ").append(description).append("\n")
                    .append(_911_TEXT).append("§c(§r" + respondingOfficerUsername).append(TO_TEXT)
                    .append("§r" + playerUsername + "§c):§r ").append(PLEASE_KEEP_CALM_TEXT);
        }
    }
}