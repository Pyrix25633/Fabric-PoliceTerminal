package net.rupyber_studios.police_terminal.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.text.Text;
import net.rupyber_studios.rupyber_database_api.util.Callsign;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class UnusedCallsignArgumentType implements ArgumentType<String> {
    private static final SimpleCommandExceptionType INVALID_UNUSED_CALLSIGN_EXCEPTION = new SimpleCommandExceptionType(
            Text.translatable("argument.unused_callsign.invalid"));

    @Override
    public String parse(@NotNull StringReader reader) throws CommandSyntaxException {
        String string = reader.readString();
        boolean inUse;
        inUse = Callsign.isInUse(string);
        if(!inUse && Callsign.isValid(string)) return string;
        throw INVALID_UNUSED_CALLSIGN_EXCEPTION.createWithContext(reader);
    }

    @Override
    public Collection<String> getExamples() {
        return new ArrayList<>();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(@NotNull CommandContext<S> context, SuggestionsBuilder builder) {
        return Suggestions.empty();
    }
}