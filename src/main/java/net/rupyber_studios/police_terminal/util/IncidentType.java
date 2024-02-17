package net.rupyber_studios.police_terminal.util;

import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.rupyber_studios.police_terminal.config.ModConfig;

import java.util.HashMap;

public class IncidentType {
    @ConfigEntry.Gui.Excluded
    public static HashMap<Integer, IncidentType> incidentTypes;

    public static void loadIncidentTypes() {
        incidentTypes = new HashMap<>();
        for(IncidentType incidentType : ModConfig.INSTANCE.incidentTypes) {
            incidentTypes.put(incidentType.id, incidentType);
        }
    }

    public static IncidentType fromId(int id) {
        return incidentTypes.get(id);
    }

    public IncidentType() {}

    public IncidentType(int id, String code, int color, String description) {
        this.id = id;
        this.code = code;
        this.color = color;
        this.description = description;
    }

    public int id = 10;

    public String code = "New Code";

    @ConfigEntry.ColorPicker
    public int color = 0x55FF55;

    public String description = "Description";
}