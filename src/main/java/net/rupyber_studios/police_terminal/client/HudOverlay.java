package net.rupyber_studios.police_terminal.client;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.rupyber_studios.police_terminal.config.ModConfig;
import net.rupyber_studios.police_terminal.util.Callsign;

public class HudOverlay implements HudRenderCallback {
    private static final int WHITE_COLOR = 0xFFFFFF;
    private static final Text STATUS_TEXT = Text.translatable("text.hud.police_terminal.status");
    private static final Text RANK_TEXT = Text.translatable("text.hud.police_terminal.rank");
    private static final Text CALLSIGN_TEXT = Text.translatable("text.hud.police_terminal.callsign");

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
            Text status = STATUS_TEXT.copy().append(Text.translatable("text.hud.police_terminal.status.out_of_service"));
            Text rank = RANK_TEXT.copy().append(ModConfig.INSTANCE.ranks.get(0).rank);
            Text callsign = CALLSIGN_TEXT.copy().append("§9" + randomCallsign);
            int statusWidth = client.textRenderer.getWidth(status);
            int rankWidth = client.textRenderer.getWidth(rank);
            int callsignWidth = client.textRenderer.getWidth(callsign);
            int hudWidth = statusWidth, hudHeight = 30;
            if(rankWidth > hudWidth) hudWidth = rankWidth;
            if(callsignWidth > hudWidth) hudWidth = callsignWidth;

            int backgroundX, backgroundY;
            int statusX, statusY;
            int rankX, rankY, rankRankX, rankRankY;
            int callsignX, callsignY;

            // Calculate x coordinates
            statusX = switch(ModConfig.INSTANCE.hudHorizontal) {
                case LEFT -> ModConfig.INSTANCE.hudHorizontalDistance;
                case CENTER -> (width - hudWidth) / 2;
                case RIGHT -> width - hudWidth - ModConfig.INSTANCE.hudHorizontalDistance;
            };
            backgroundX = statusX - 5;
            rankX = statusX;
            rankRankX = rankX + client.textRenderer.getWidth(RANK_TEXT);
            callsignX = statusX;

            // Calculate y coordinates
            statusY = switch(ModConfig.INSTANCE.hudVertical) {
                case TOP -> ModConfig.INSTANCE.hudVerticalDistance;
                case CENTER -> (height - hudHeight) / 2;
                case BOTTOM -> height - hudHeight - ModConfig.INSTANCE.hudVerticalDistance;
            };
            backgroundY = statusY - 5;
            rankY = statusY + 10;
            rankRankY = rankY;
            callsignY = statusY + 20;

            // Draw background
            context.fill(backgroundX, backgroundY, backgroundX + hudWidth + 10, backgroundY + hudHeight + 10,
                    ModConfig.INSTANCE.hudBackground);
            // Draw texts
            context.drawText(client.textRenderer, status, statusX, statusY, WHITE_COLOR, true);
            context.drawText(client.textRenderer, RANK_TEXT.copy(), rankX, rankY, WHITE_COLOR, true);
            context.drawText(client.textRenderer, Text.literal(ModConfig.INSTANCE.ranks.get(0).rank),
                    rankRankX, rankRankY, ModConfig.INSTANCE.ranks.get(0).color, true);
            context.drawText(client.textRenderer, callsign, callsignX, callsignY, WHITE_COLOR, true);
        }
    }
}