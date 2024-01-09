package net.rupyber_studios.police_terminal.util;

import net.minecraft.text.Text;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public enum Status {
    OUT_OF_SERVICE, AVAILABLE, ON_PATROL, BUSY, EN_ROUTE, ON_SCENE, EMERGENCY;

    @Contract(value = " -> new", pure = true)
    public @NotNull Text getText() {
        return Text.translatable("text.hud.police_terminal.status." + this.name().toLowerCase());
    }
}