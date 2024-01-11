package net.rupyber_studios.police_terminal.util;

import net.minecraft.text.Text;
import net.minecraft.util.StringIdentifiable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum Status implements StringIdentifiable {
    OUT_OF_SERVICE, AVAILABLE, ON_PATROL, BUSY, EN_ROUTE, ON_SCENE, EMERGENCY;

    @Contract(pure = true)
    public static @Nullable Status fromId(int id) {
        if(id == 0) return null;
        return values()[id - 1];
    }

    @Contract(value = " -> new", pure = true)
    public @NotNull Text getText() {
        return Text.translatable("text.hud.police_terminal.status." + this.name().toLowerCase());
    }

    public int getId() {
        return this.ordinal() + 1;
    }

    @Override
    public @NotNull String asString() {
        return this.name().toLowerCase();
    }
}