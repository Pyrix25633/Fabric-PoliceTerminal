package net.rupyber_studios.police_terminal.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import net.rupyber_studios.police_terminal.client.HudPosition;
import net.rupyber_studios.rupyber_database_api.config.PoliceTerminalConfig;
import net.rupyber_studios.rupyber_database_api.table.IncidentType;
import net.rupyber_studios.rupyber_database_api.table.Rank;
import net.rupyber_studios.rupyber_database_api.table.ResponseCode;

import java.util.List;

@Config(name = "police_terminal")
public class ModConfig extends PoliceTerminalConfig implements ConfigData {
    @ConfigEntry.Gui.Excluded
    public static ModConfig INSTANCE;

    public static void init() {
        AutoConfig.register(ModConfig.class, JanksonConfigSerializer::new);
        INSTANCE = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
    }

    // default

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("default")
    @Comment("If true coordinates will be shown in the hud")
    public boolean hudShowCoordinates = true;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("default")
    @Comment("The horizontal position of the hud")
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    public HudPosition.Horizontal hudHorizontal = HudPosition.Horizontal.LEFT;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("default")
    @Comment("The vertical position of the hud")
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    public HudPosition.Vertical hudVertical = HudPosition.Vertical.TOP;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("default")
    @Comment("The hud horizontal distance from the screen border")
    @ConfigEntry.BoundedDiscrete(min = 0, max = 30)
    public int hudHorizontalDistance = 5;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("default")
    @Comment("The hud vertical distance from the screen border")
    @ConfigEntry.BoundedDiscrete(min = 0, max = 30)
    public int hudVerticalDistance = 5;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("default")
    @Comment("The background color of the hud")
    @ConfigEntry.ColorPicker(allowAlpha = true)
    public int hudBackground = 0x55333333;

    // duty

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("duty")
    @Comment("If true only admins will be able to use the rank command")
    public boolean rankCommandRequiresAdmin = false;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("duty")
    @Comment("If true an officer will be able to promote himself and other players to a rank higher than his rank and use the rank command on players with a higher rank (admins can violate these rules)")
    public boolean officerCanGrantRankHigherThanHis = false;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("duty")
    @Comment("Only officers with a rank id greater or equal than the one set below will be able to use the rank command")
    public int minimumRankIdForRankCommand = 10;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("duty")
    @Comment("If true only admins will be able to use the rank command (except for 'request' subcommand)")
    public boolean callsignCommandRequiresAdmin = false;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("duty")
    @Comment("Only officers with a rank id greater or equal than the one set below will be able to use the callsign command (except for 'request' subcommand)")
    public int minimumRankIdForCallsignCommand = 10;

    // web

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("web")
    @Comment("The port used to connect to the web terminal (<address>:<port>)")
    public int port = 3000;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("web")
    @Comment("If enabled the server will use the https protocol (strongly recommended), the options below should be set")
    public boolean https = false;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("web")
    @Comment("The https certificate path, if invalid the server will default to http")
    public String httpsCertificate = "";

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("web")
    @Comment("The https certificate password, if invalid the server will default to http")
    public String httpsPassword = "";

    {
        ranks = List.of(
                new Rank(80, "Chief", false, 0xFF6347),
                new Rank(70, "Assistant Chief", false, 0xFFA07A),
                new Rank(60, "Deputy Chief", false, 0xFFD700),
                new Rank(50, "Commander", false, 0x6495ED),
                new Rank(43, "Captain III", false, 0x87CEFA),
                new Rank(42, "Captain II", false, 0x00CED1),
                new Rank(41, "Captain I", false, 0x4682B4),
                new Rank(32, "Lieutenant II", false, 0x20B2AA),
                new Rank(31, "Lieutenant I", false, 0x2E8B57),
                new Rank(25, "Detective III", false, 0xDC143C),
                new Rank(24, "Sergeant II", false, 0x8B4513),
                new Rank(23, "Detective II", false, 0xCD853F),
                new Rank(22, "Sergeant I", false, 0x008000),
                new Rank(21, "Detective I", false, 0x6B8E23),
                new Rank(14, "Officer III+1", false, 0x00FA9A),
                new Rank(13, "Officer III", false, 0x3CB371),
                new Rank(12, "Officer II", false, 0x00FF7F),
                new Rank(11, "Officer I", false, 0x66CDAA),
                new Rank(2, "911 Operator II", true, 0x346AB5),
                new Rank(1, "911 Operator I", true, 0x346AB5)
        );
        callsignAreaUnitBeat = true;
        callsignBeatUnit = true;
        callsignUnitBeat = false;
        callsignAreaMin = 1;
        callsignAreaMax = 99;
        callsignBeatMin = 1;
        callsignBeatMax = 199;
        callsignUnits = List.of(
                "Adam", "Charles", "David", "Edward", "George",
                "Lincoln", "Mary", "Nora", "Ocean", "Robert", "Tom", "William"
        );
        responseCodes = List.of(
                new ResponseCode(1, "Code 2", 0xAAAAAA, "Respond without Lights and Sirens"),
                new ResponseCode(2, "Code 2-High", 0x55FFFF, "Respond with Lights, but without Sirens"),
                new ResponseCode(3, "Code 3", 0x0000AA, "Respond with Lights and Sirens"),
                new ResponseCode(99, "Code 99", 0xAA0000, "All Units to respond, High-Level Situation")
        );
        incidentTypes = List.of(
                new IncidentType(211, "211", 0xAA0000, "Robbery"),
                new IncidentType(311, "311", 0xFFFF55, "Indecent Exposure"),
                new IncidentType(390, "390", 0xFFFF55, "Drunk Male"),
                new IncidentType(391, "390W", 0xFFFF55, "Drunk Female"),
                new IncidentType(415, "415", 0xFFAA00, "Disturbance"),
                new IncidentType(459, "459", 0xAA0000, "Burglary"),
                new IncidentType(484, "484", 0xAA0000, "Theft"),
                new IncidentType(485, "484PS", 0xAA0000, "Purse Snatching"),
                new IncidentType(502, "502", 0xFFAA00, "Under-the-Influence Driver"),
                new IncidentType(507, "507", 0xFFFF55, "Minor Disturbance"),
                new IncidentType(508, "507FC", 0xFFFF55, "Fire Crackers"),
                new IncidentType(586, "586", 0xFFAA00, "Illegal Parking"),
                new IncidentType(587, "586E", 0xFFAA00, "Car parked in Driveway")
        );
        recordsPerPage = 15;
    }
}