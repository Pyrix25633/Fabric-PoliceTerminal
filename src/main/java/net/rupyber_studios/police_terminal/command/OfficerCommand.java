package net.rupyber_studios.police_terminal.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.rupyber_studios.police_terminal.PoliceTerminal;
import net.rupyber_studios.rupyber_database_api.table.Player;
import net.rupyber_studios.rupyber_database_api.util.PlayerInfo;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public class OfficerCommand {
    private static final Text OFFICER_TEXT = Text.translatable("commands.officer.success.officer");
    private static final Text STATUS_TEXT = Text.translatable("commands.officer.success.status");
    private static final Text RANK_TEXT = Text.translatable("commands.officer.success.rank");
    private static final Text CALLSIGN_TEXT = Text.translatable("commands.officer.success.callsign");
    private static final Text NOT_OFFICER_TEXT = Text.translatable("commands.officer.success.not_officer");

    public static void register(@NotNull CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess registryAccess,
                                CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("officer")
                .then(CommandManager.argument("username", EntityArgumentType.player())
                        .executes(OfficerCommand::execute))
        );
    }

    private static int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "username");
        try {
            PlayerInfo info = Player.selectPlayerInfoFromUuid(player.getUuid());
            Text feedback;
            if(info.rank != null && info.status != null)
                feedback = OFFICER_TEXT.copy().append(player.getGameProfile().getName()).append(":")
                        .append(STATUS_TEXT).append(info.status.getText())
                        .append(RANK_TEXT).append(Text.literal(info.rank.rank).withColor(info.rank.color))
                        .append(CALLSIGN_TEXT).append(info.callsign != null ? ("§9" + info.callsign) : "§7-");
            else
                feedback = Text.literal(player.getGameProfile().getName()).append(NOT_OFFICER_TEXT);
            context.getSource().sendFeedback(() -> feedback, false);
        } catch(SQLException e) {
            PoliceTerminal.LOGGER.error("Could not display info for player: ", e);
            return 0;
        }
        return 1;
    }
}