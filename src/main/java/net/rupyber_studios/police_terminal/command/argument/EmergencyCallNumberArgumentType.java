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
import net.rupyber_studios.police_terminal.PoliceTerminal;
import net.rupyber_studios.rupyber_database_api.table.EmergencyCall;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class EmergencyCallNumberArgumentType implements ArgumentType<Integer> {
    private static final SimpleCommandExceptionType INVALID_EMERGENCY_CALL_NUMBER_EXCEPTION =
            new SimpleCommandExceptionType(Text.translatable("argument.emergency_call_number.invalid"));
    public static List<Integer> values = new ArrayList<>();
    public static List<String> stringValues = new ArrayList<>();

    public static void init() {
        try {
            values = EmergencyCall.selectCallNumberWhereClosedFalse();
            stringValues = new ArrayList<>();
            for(Integer value : values)
                stringValues.add(String.valueOf(value));
        } catch(SQLException e) {
            PoliceTerminal.LOGGER.error("Could not list all emergency call numbers: ", e);
        }
    }

    @Override
    public Integer parse(@NotNull StringReader reader) throws CommandSyntaxException {
        Integer integer = reader.readInt();
        if(values.contains(integer)) return integer;
        throw INVALID_EMERGENCY_CALL_NUMBER_EXCEPTION.createWithContext(reader);
    }

    @Override
    public Collection<String> getExamples() {
        return stringValues;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(@NotNull CommandContext<S> context, SuggestionsBuilder builder) {
        return context.getSource() instanceof CommandSource ? CommandSource.suggestMatching(stringValues, builder) : Suggestions.empty();
    }
}