package net.rupyber_studios.police_terminal.client;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class HudOverlay implements HudRenderCallback {
    private static final int WHITE_COLOR = 0xFFFFFF;
    private static final Text STATUS_TEXT = Text.translatable("text.hud.police_terminal.status");

    @Override
    public void onHudRender(DrawContext context, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        if(client != null) {
            int width = client.getWindow().getScaledWidth();
            int height = client.getWindow().getScaledHeight();

            // TODO: configurable position
            context.drawText(client.textRenderer, STATUS_TEXT, 10, 10, WHITE_COLOR, true);
        }
    }
}