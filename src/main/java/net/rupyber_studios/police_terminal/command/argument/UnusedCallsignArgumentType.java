package net.rupyber_studios.police_terminal.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.text.Text;
import net.rupyber_studios.police_terminal.database.DatabaseSelector;
import net.rupyber_studios.police_terminal.util.Callsign;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
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
        try {
            inUse = DatabaseSelector.isCallsignInUse(string);
        } catch(SQLException e) {
            inUse = true;
        }
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