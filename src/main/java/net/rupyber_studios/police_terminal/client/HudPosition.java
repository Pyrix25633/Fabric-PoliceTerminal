package net.rupyber_studios.police_terminal.client;

import net.minecraft.text.Text;

public class HudPosition {
    public enum Horizontal {
        LEFT, CENTER, RIGHT;

        @Override
        public String toString() {
            return switch(this) {
                case LEFT -> Text.translatable("text.autoconfig.police_terminal.enum.horizontal.left").getString();
                case CENTER -> Text.translatable("text.autoconfig.police_terminal.enum.horizontal.center").getString();
                case RIGHT -> Text.translatable("text.autoconfig.police_terminal.enum.horizontal.right").getString();
            };
        }
    }

    public enum Vertical {
        TOP, CENTER, BOTTOM;

        @Override
        public String toString() {
            return switch(this) {
                case TOP -> Text.translatable("text.autoconfig.police_terminal.enum.vertical.top").getString();
                case CENTER -> Text.translatable("text.autoconfig.police_terminal.enum.vertical.center").getString();
                case BOTTOM -> Text.translatable("text.autoconfig.police_terminal.enum.vertical.bottom").getString();
            };
        }
    }
}