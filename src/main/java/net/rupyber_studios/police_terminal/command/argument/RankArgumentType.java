package net.rupyber_studios.police_terminal.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import net.minecraft.util.StringIdentifiable;
import net.rupyber_studios.police_terminal.util.Rank;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

//TODO: continue

public class RankArgumentType implements ArgumentType<Rank> {
    private final ArrayList<String> values = new ArrayList<>();

    public RankArgumentType() {
        for(Rank rank : Rank.ranks.values())
            values.add(rank.rank);
    }

    @Override
    public Rank parse(StringReader reader) throws CommandSyntaxException {
        return null;
    }
}