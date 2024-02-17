package net.rupyber_studios.police_terminal.util;

import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.rupyber_studios.police_terminal.config.ModConfig;

import java.util.HashMap;

public class ResponseCode {
    @ConfigEntry.Gui.Excluded
    public static HashMap<Integer, ResponseCode> responseCodes;

    public static void loadResponseCodes() {
        responseCodes = new HashMap<>();
        for(ResponseCode responseCode : ModConfig.INSTANCE.responseCodes) {
            responseCodes.put(responseCode.id, responseCode);
        }
    }

    public static ResponseCode fromId(int id) {
        return responseCodes.get(id);
    }

    public ResponseCode() {}

    public ResponseCode(int id, String code, int color, String description) {
        this.id = id;
        this.code = code;
        this.color = color;
        this.description = description;
    }

    public int id = 10;

    public String code = "New Code";

    @ConfigEntry.ColorPicker
    public int color = 0xAAAAAA;

    public String description = "Description";
}