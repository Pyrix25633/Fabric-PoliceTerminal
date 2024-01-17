package net.rupyber_studios.police_terminal.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.rupyber_studios.police_terminal.PoliceTerminal;
import net.rupyber_studios.police_terminal.command.argument.OnlineCallsignArgumentType;
import net.rupyber_studios.police_terminal.command.argument.UnusedCallsignArgumentType;
import net.rupyber_studios.police_terminal.database.DatabaseManager;
import net.rupyber_studios.police_terminal.networking.packet.SendCallsignS2CPacket;
import net.rupyber_studios.police_terminal.util.Callsign;
import net.rupyber_studios.police_terminal.util.Rank;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public class CallsignCommand {
    private static final Text YOUR_CALLSIGN_IS_NOW_TEXT = Text.translatable("commands.callsign.success.your_callsign_is_now");

    public static void register(@NotNull CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess registryAccess,
                                CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("callsign").then(CommandManager.literal("request").requires((source) -> {
            try {
                ServerPlayerEntity player = source.getPlayer();
                if(player == null) return false;
                Rank rank = DatabaseManager.getPlayerRank(source.getPlayer().getUuid());
                return rank != null;
            } catch(Exception ignored) {
                return false;
            }
        }).executes((context) -> {
            try {
                ServerPlayerEntity player = context.getSource().getPlayer();
                if(player == null) return 0;
                String callsign = Callsign.createRandomCallsign();
                while(DatabaseManager.isCallsignInUse(callsign))
                    callsign = Callsign.createRandomCallsign();
                DatabaseManager.setPlayerCallsign(player.getUuid(), callsign, false);
                OnlineCallsignArgumentType.init();
                SendCallsignS2CPacket.send(player, callsign);
                context.getSource().getServer().getCommandManager().sendCommandTree(player);
                Text feedback = YOUR_CALLSIGN_IS_NOW_TEXT.copy().append("§9" + callsign);
                context.getSource().sendFeedback(() -> feedback, false);
            } catch(SQLException e) {
                PoliceTerminal.LOGGER.error("Could not assign a callsign to player: ", e);
                return 0;
            }
            return 1;
        }).then(CommandManager.argument("callsign", new UnusedCallsignArgumentType()).executes((context) -> {
            try {
                ServerPlayerEntity player = context.getSource().getPlayer();
                if(player == null) return 0;
                String callsign = context.getArgument("callsign", String.class);
                DatabaseManager.setPlayerCallsign(player.getUuid(), callsign, false);
                OnlineCallsignArgumentType.init();
                SendCallsignS2CPacket.send(player, callsign);
                context.getSource().getServer().getCommandManager().sendCommandTree(player);
                Text feedback = YOUR_CALLSIGN_IS_NOW_TEXT.copy().append("§9" + callsign);
                context.getSource().sendFeedback(() -> feedback, false);
            } catch(SQLException e) {
                PoliceTerminal.LOGGER.error("Could not assign a callsign to player: ", e);
                return 0;
            }
            return 1;
        }))));
    }
}