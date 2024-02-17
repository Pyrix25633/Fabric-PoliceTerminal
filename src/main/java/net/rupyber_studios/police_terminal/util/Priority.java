package net.rupyber_studios.police_terminal.util;

import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import net.minecraft.util.StringIdentifiable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum Priority implements StringIdentifiable {
    LOW, PRIORITY, MAJOR;

    @Contract(pure = true)
    public static @Nullable Priority fromId(int id) {
        if(id == 0) return null;
        return values()[id - 1];
    }

    @Contract(value = " -> new", pure = true)
    public @NotNull Text getText() {
        return Text.translatable("text.hud.police_terminal.priority." + this.name().toLowerCase());
    }

    public int getId() {
        return this.ordinal() + 1;
    }

    @Override
    public @NotNull String asString() {
        return this.name().toLowerCase();
    }

    public @NotNull Pair<String, Integer> getData() {
        return switch(this) {
            case LOW -> new Pair<>("Low", 0x55FF55);
            case PRIORITY -> new Pair<>("Priority", 0xFFFF55);
            case MAJOR -> new Pair<>("Major", 0xFF5555);
        };
    }
}