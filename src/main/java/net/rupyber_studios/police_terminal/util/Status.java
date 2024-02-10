package net.rupyber_studios.police_terminal.util;

import net.minecraft.text.Text;
import net.minecraft.util.Pair;
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

    public Pair<String, Integer> getData() {
        return switch(this) {
            case OUT_OF_SERVICE -> new Pair<>("Out Of Service", 0xFFFF55);
            case AVAILABLE -> new Pair<>("Available", 0x55FF55);
            case ON_PATROL -> new Pair<>("On Patrol", 0x00AA00);
            case BUSY -> new Pair<>("Busy", 0xFFAA00);
            case EN_ROUTE -> new Pair<>("En Route", 0x55FFFF);
            case ON_SCENE -> new Pair<>("On Scene", 0x00AAAA);
            case EMERGENCY -> new Pair<>("Emergency", 0xAA0000);
        };
    }
}