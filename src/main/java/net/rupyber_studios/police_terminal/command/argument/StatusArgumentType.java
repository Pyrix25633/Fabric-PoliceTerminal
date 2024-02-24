package net.rupyber_studios.police_terminal.command.argument;

import com.mojang.serialization.Codec;
import net.minecraft.command.argument.EnumArgumentType;
import net.minecraft.util.StringIdentifiable;
import net.rupyber_studios.rupyber_database_api.util.Status;

public class StatusArgumentType extends EnumArgumentType<Status> {
    private static final Codec<Status> CODEC = StringIdentifiable.createCodec(Status::values);

    public StatusArgumentType() {
        super(CODEC, Status::values);
    }
}