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
import net.rupyber_studios.rupyber_database_api.util.Callsign;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class OnlineCallsignArgumentType implements ArgumentType<String> {
    private static final SimpleCommandExceptionType INVALID_ONLINE_CALLSIGN_EXCEPTION = new SimpleCommandExceptionType(
            Text.translatable("argument.online_callsign.invalid"));
    public static List<String> values = new ArrayList<>();

    public static void init() {
        values = Callsign.selectAll();
    }

    @Override
    public String parse(@NotNull StringReader reader) throws CommandSyntaxException {
        String string = reader.readString();
        if(values.contains(string)) return string;
        throw INVALID_ONLINE_CALLSIGN_EXCEPTION.createWithContext(reader);
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