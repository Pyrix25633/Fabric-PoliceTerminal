package net.rupyber_studios.police_terminal.client;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.rupyber_studios.police_terminal.config.ModConfig;
import net.rupyber_studios.police_terminal.util.Callsign;
import net.rupyber_studios.police_terminal.util.Status;
import org.jetbrains.annotations.NotNull;

public class HudOverlay implements HudRenderCallback {
    public static Status status = Status.OUT_OF_SERVICE;

    private static final int WHITE_COLOR = 0xFFFFFF;
    private static final Text STATUS_TEXT = Text.translatable("text.hud.police_terminal.status");
    private static final Text RANK_TEXT = Text.translatable("text.hud.police_terminal.rank");
    private static final Text CALLSIGN_TEXT = Text.translatable("text.hud.police_terminal.callsign");
    private static final Text COORDINATES_TEXT = Text.translatable("text.hud.police_terminal.coordinates");

    // TODO: remove
    private static final String randomCallsign = Callsign.createRandomCallsign();

    @Override
    public void onHudRender(DrawContext context, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        if(client != null) {
            if(client.inGameHud.getDebugHud().shouldShowDebugHud() || client.inGameHud.getSpectatorHud().isOpen() ||
                    client.options.hudHidden) return;

            int width = client.getWindow().getScaledWidth(), height = client.getWindow().getScaledHeight();

            // Calculate widths
            Text statusText = STATUS_TEXT.copy().append(status.getText());
            Text rankText = RANK_TEXT.copy().append(ModConfig.INSTANCE.ranks.get(0).rank);
            Text callsignText = CALLSIGN_TEXT.copy().append("§9" + randomCallsign);
            int statusWidth = client.textRenderer.getWidth(statusText);
            int rankWidth = client.textRenderer.getWidth(rankText);
            int callsignWidth = client.textRenderer.getWidth(callsignText);
            int hudWidth = statusWidth, hudHeight = 38;
            if(rankWidth > hudWidth) hudWidth = rankWidth;
            if(callsignWidth > hudWidth) hudWidth = callsignWidth;

            Text coordinates = null;
            int coordinatesWidth;
            if(ModConfig.INSTANCE.hudShowCoordinates) {
                coordinates = COORDINATES_TEXT.copy().append("§e" + getCoordinates(client));
                coordinatesWidth = client.textRenderer.getWidth(coordinates);
                if(coordinatesWidth > hudWidth) hudWidth = coordinatesWidth;
                hudHeight += 10;
            }

            hudWidth += 10;

            int backgroundX, backgroundY;
            int statusX, statusY;
            int rankX, rankY, rankRankX, rankRankY;
            int callsignX, callsignY;
            int coordinatesX, coordinatesY;

            // Calculate x coordinates
            backgroundX = switch(ModConfig.INSTANCE.hudHorizontal) {
                case LEFT -> ModConfig.INSTANCE.hudHorizontalDistance;
                case CENTER -> (width - hudWidth) / 2;
                case RIGHT -> width - hudWidth - ModConfig.INSTANCE.hudHorizontalDistance;
            };
            statusX = backgroundX + 5;
            rankX = statusX;
            rankRankX = rankX + client.textRenderer.getWidth(RANK_TEXT);
            callsignX = statusX;

            // Calculate y coordinates
            backgroundY = switch(ModConfig.INSTANCE.hudVertical) {
                case TOP -> ModConfig.INSTANCE.hudVerticalDistance;
                case CENTER -> (height - hudHeight) / 2;
                case BOTTOM -> height - hudHeight - ModConfig.INSTANCE.hudVerticalDistance;
            };
            statusY = backgroundY + 5;
            rankY = statusY + 10;
            rankRankY = rankY;
            callsignY = statusY + 20;

            // Draw background
            context.fill(backgroundX, backgroundY, backgroundX + hudWidth + 10, backgroundY + hudHeight + 10,
                    ModConfig.INSTANCE.hudBackground);
            // Draw texts
            context.drawText(client.textRenderer, statusText, statusX, statusY, WHITE_COLOR, true);
            context.drawText(client.textRenderer, RANK_TEXT.copy(), rankX, rankY, WHITE_COLOR, true);
            context.drawText(client.textRenderer, Text.literal(ModConfig.INSTANCE.ranks.get(0).rank),
                    rankRankX, rankRankY, ModConfig.INSTANCE.ranks.get(0).color, true);
            context.drawText(client.textRenderer, callsignText, callsignX, callsignY, WHITE_COLOR, true);

            if(ModConfig.INSTANCE.hudShowCoordinates) {
                coordinatesX = statusX;
                coordinatesY = statusY + 30;
                context.drawText(client.textRenderer, coordinates, coordinatesX, coordinatesY, WHITE_COLOR, true);
            }
        }
    }

    private @NotNull String getCoordinates(@NotNull MinecraftClient client) {
        if(client.player != null) {
            Vec3d pos = client.player.getPos();
            return (int)pos.x + " " + (int)pos.y + " " + (int)pos.z;
        }
        return "0 0 0";
    }
}