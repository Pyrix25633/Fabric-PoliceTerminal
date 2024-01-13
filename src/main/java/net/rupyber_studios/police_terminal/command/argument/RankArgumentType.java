package net.rupyber_studios.police_terminal.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import net.rupyber_studios.police_terminal.util.Rank;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class RankArgumentType implements ArgumentType<Rank> {
    private static final SimpleCommandExceptionType INVALID_RANK_EXCEPTION = new SimpleCommandExceptionType(
            Text.translatable("argument.rank.invalid"));
    public static ArrayList<String> values = new ArrayList<>();

    public static void init() {
        for(Rank rank : Rank.ranks.values())
            values.add(rank.rank.toLowerCase().replaceAll(" ", "_"));
    }

    @Override
    public Rank parse(@NotNull StringReader reader) throws CommandSyntaxException {
        String string = reader.readString();
        for(Rank rank : Rank.ranks.values()) {
            if(rank.rank.toLowerCase().replaceAll(" ", "_").equals(string)) return rank;
        }
        throw INVALID_RANK_EXCEPTION.createWithContext(reader);
    }

    @Override
    public Collection<String> getExamples() {
        return values;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(@NotNull CommandContext<S> context, SuggestionsBuilder builder) {
        return context.getSource() instanceof CommandSource ? CommandSource.suggestMatching(values, builder) : Suggestions.empty();
    }
}