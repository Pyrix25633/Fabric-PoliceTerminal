package net.rupyber_studios.police_terminal.client;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class HudOverlay implements HudRenderCallback {
    private static final int WHITE_COLOR = 0xFFFFFF;
    private static final Text STATUS_TEXT = Text.translatable("text.hud.police_terminal.status");
    private static final Text RANK_TEXT = Text.translatable("text.hud.police_terminal.rank");
    private static final Text CALLSIGN_TEXT = Text.translatable("text.hud.police_terminal.callsign");

    @Override
    public void onHudRender(DrawContext context, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        if(client != null) {
            int width = client.getWindow().getScaledWidth();
            int height = client.getWindow().getScaledHeight();

            // TODO: configurable position
            // TODO: rank and callsign
            context.drawText(client.textRenderer, STATUS_TEXT.copy().append(Text.translatable("text.hud.police_terminal.status.out_of_service")),
                    10, 10, WHITE_COLOR, true);
            context.drawText(client.textRenderer, RANK_TEXT.copy().append(Text.translatable("text.hud.police_terminal.status.out_of_service")),
                    10, 10, WHITE_COLOR, true);
            context.drawText(client.textRenderer, CALLSIGN_TEXT.copy().append(Text.translatable("text.hud.police_terminal.status.out_of_service")),
                    10, 10, WHITE_COLOR, true);
        }
    }
}