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
import net.rupyber_studios.police_terminal.command.argument.EmergencyCallNumberArgumentType;
import net.rupyber_studios.rupyber_database_api.table.EmergencyCall;
import net.rupyber_studios.rupyber_database_api.table.Player;
import net.rupyber_studios.rupyber_database_api.table.Rank;
import net.rupyber_studios.rupyber_database_api.util.Officer;
import net.rupyber_studios.rupyber_database_api.util.Status;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    private static final Text NO_NEED_FOR_POLICE_TEXT =
            Text.translatable("commands.emergency.success.no_need_for_police");
    private static final Text CLOSED_CALL_TEXT = Text.translatable("commands.emergency.success.closed_call");

    public static void register(@NotNull CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess registryAccess,
                                CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("emergency")
                .then(CommandManager.literal("ignore")
                        .requires(EmergencyCommand::canExecuteIgnore)
                        .then(CommandManager.argument("callNumber", new EmergencyCallNumberArgumentType())
                                .executes(EmergencyCommand::executeIgnore)))
                .then(CommandManager.literal("call")
                        .requires(EmergencyCommand::canExecuteCall)
                        .then(CommandManager.argument("description", StringArgumentType.greedyString())
                                .executes(EmergencyCommand::executeCall)))
        );
        dispatcher.register(CommandManager.literal("911")
                .then(CommandManager.literal("ignore")
                        .requires(EmergencyCommand::canExecuteIgnore)
                        .then(CommandManager.argument("callNumber", new EmergencyCallNumberArgumentType())
                                .executes(EmergencyCommand::executeIgnore)))
                .then(CommandManager.literal("call")
                        .requires(EmergencyCommand::canExecuteCall)
                        .then(CommandManager.argument("description", StringArgumentType.greedyString())
                                .executes(EmergencyCommand::executeCall)))
        );
    }

    private static boolean canExecuteCall(@NotNull ServerCommandSource source) {
            ServerPlayerEntity player = source.getPlayer();
            return player != null;
    }

    private static boolean canExecuteIgnore(@NotNull ServerCommandSource source) {
        ServerPlayerEntity player = source.getPlayer();
        if(player == null) return false;
        Rank rank = Officer.selectRankWhereUuid(player.getUuid());
        return (rank != null && rank.emergencyOperator);
    }

    private static int executeCall(@NotNull CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if(player == null) return 0;
        String description = StringArgumentType.getString(context, "description");
        ServerPlayerEntity respondingOfficer = selectRespondingOfficer(context.getSource().getServer());
        Text feedback = buildFeedbackCall(player, respondingOfficer, description);
        if(respondingOfficer != null) {
            Officer.updateStatusWhereUuid(respondingOfficer.getUuid(), Status.BUSY);
            int callNumber = EmergencyCall.insertAndReturnCallNumber(player.getUuid(), respondingOfficer.getUuid(),
                    player.getPos(), description);
            respondingOfficer.sendMessage(INCOMING_CALL_TEXT.copy().append(callNumber + "\n").append(feedback));
            EmergencyCallNumberArgumentType.init();
        }
        if(player != respondingOfficer)
            context.getSource().sendFeedback(() -> feedback, false);
        return 1;
    }

    private static int executeIgnore(@NotNull CommandContext<ServerCommandSource> context)
            throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if(player == null) return 0;
        int callNumber = context.getArgument("callNumber", Integer.class);
        EmergencyCall emergencyCall = EmergencyCall.selectWhereCallNumber(callNumber);
        if(emergencyCall == null) return 0;
        if(!canOfficerIgnoreEmergencyCall(player, emergencyCall)) return 0;
        EmergencyCall.updateClosedTrue(emergencyCall.id);
        ServerPlayerEntity caller = selectCaller(context.getSource().getServer(), emergencyCall.callerId);
        if(caller == null) return 0;
        Text feedback = buildFeedbackIgnore(player, caller);
        context.getSource().sendFeedback(() -> feedback.copy().append("\n").append(CLOSED_CALL_TEXT)
                        .append(callNumber + ""), false);
        if(caller != player)
            caller.sendMessage(feedback);
        return 1;
    }

    private static @Nullable ServerPlayerEntity selectRespondingOfficer(@NotNull MinecraftServer server) {
        ServerPlayerEntity emergencyOperator;
        do {
            UUID emergencyOperatorUuid = Officer.selectAvailableEmergencyOperator();
            if(emergencyOperatorUuid == null) return null;
            emergencyOperator = server.getPlayerManager().getPlayer(emergencyOperatorUuid);
        } while (emergencyOperator == null);
        return emergencyOperator;
    }

    private static boolean canOfficerIgnoreEmergencyCall(@NotNull ServerPlayerEntity officer,
                                                         EmergencyCall emergencyCall) {
        Integer officerId = Player.selectIdWhereUuid(officer.getUuid());
        if(officerId == null) return false;
        if(emergencyCall.closedAt != null) return false;
        return emergencyCall.responderId == officerId;
    }

    private static @Nullable ServerPlayerEntity selectCaller(@NotNull MinecraftServer server, int callerId) {
        UUID callerUuid = Player.selectUuid(callerId);
        if(callerUuid == null) return null;
        return server.getPlayerManager().getPlayer(callerUuid);
    }

    private static Text buildFeedbackCall(@NotNull ServerPlayerEntity player,
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

    private static Text buildFeedbackIgnore(@NotNull ServerPlayerEntity player, @NotNull ServerPlayerEntity caller) {
        return _911_TEXT.copy().append("§c(§r" + player.getGameProfile().getName()).append(TO_TEXT)
                .append("§r" + caller.getGameProfile().getName() + "§c):§r ").append(NO_NEED_FOR_POLICE_TEXT);
    }
}