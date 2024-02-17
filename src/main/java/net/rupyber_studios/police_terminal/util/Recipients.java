package net.rupyber_studios.police_terminal.util;

import net.minecraft.text.Text;
import net.minecraft.util.StringIdentifiable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum Recipients implements StringIdentifiable {
    NEARBY, AVAILABLE, ALL;

    @Contract(pure = true)
    public static @Nullable Recipients fromId(int id) {
        if(id == 0) return null;
        return values()[id - 1];
    }

    @Contract(value = " -> new", pure = true)
    public @NotNull Text getText() {
        return Text.translatable("text.hud.police_terminal.recipients." + this.name().toLowerCase());
    }

    public int getId() {
        return this.ordinal() + 1;
    }

    @Override
    public @NotNull String asString() {
        return this.name().toLowerCase();
    }

    public @NotNull String getData() {
        return switch(this) {
            case NEARBY -> "Nearby";
            case AVAILABLE -> "Available";
            case ALL -> "All";
        };
    }
}