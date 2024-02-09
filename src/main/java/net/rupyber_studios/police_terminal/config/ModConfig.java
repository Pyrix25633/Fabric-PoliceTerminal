package net.rupyber_studios.police_terminal.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import net.rupyber_studios.police_terminal.client.HudPosition;
import net.rupyber_studios.police_terminal.util.Rank;

import java.util.List;

@Config(name = "police_terminal")
public class ModConfig implements ConfigData {
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
    @Comment("The list of ranks used in the duty system, id and rank must be unique [do not use 0 as Java cannot be distinguish it from NULL (civilian)!], highers ones are higher in the chain of command")
    public List<Rank> ranks = List.of(
            new Rank(10, "Officer", 0x5555FF),
            new Rank(5, "Rookie", 0x55FFFF)
    );

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("duty")
    @Comment("If true only admins will be able to use the rank command")
    public boolean rankCommandRequiresAdmin = false;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("duty")
    @Comment("If true an officer will be able to promote himself and other players to a rank higher than his rank  and use the rank command on players with a higher rank (admins can violate these rules)")
    public boolean officerCanGrantRankHigherThanHis = false;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("duty")
    @Comment("Only officers with a rank id greater or equal than the one set below will be able to use the rank command")
    public int minimumRankIdForRankCommand = 10;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("duty")
    @Comment("If true callsigns like 7-Adam-22 will be used")
    public boolean callsignNumberLetterNumber = true;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("duty")
    @Comment("If true callsigns like 20-David will be used")
    public boolean callsignNumberLetter = true;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("duty")
    @Comment("If true callsigns like Sam-81 will be used")
    public boolean callsignLetterNumber = true;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("duty")
    @Comment("The minimum for the first number that is $-Adam-22 and $-David")
    public int callsignFirstNumberMin = 1;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("duty")
    @Comment("The maximum for the first number that is $-Adam-22 and $-David")
    public int callsignFirstNumberMax = 99;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("duty")
    @Comment("The minimum for the second number that is 7-Adam-$ and Sam-$")
    public int callsignSecondNumberMin = 1;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("duty")
    @Comment("The maximum for the second number that is 7-Adam-$ and Sam-$")
    public int callsignSecondNumberMax = 199;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("duty")
    @Comment("The letters used in callsigns that are 7-$-22, 20-$ and $-81")
    public List<String> callsignLetters = List.of(
            "Adam", "Boy", "Charles", "David", "Edward", "Frank", "George", "Henry", "Ida", "John", "King", "Lincoln", "Mary",
            "Nora", "Ocean", "Paul", "Queen", "Robert", "Sam", "Tom", "Union", "Victor", "William", "X-Ray", "Young", "Zebra"
    );

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

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("web")
    @Comment("The number of records per page displayed in tables")
    @ConfigEntry.BoundedDiscrete(min = 5, max = 50)
    public int recordsPerPage = 15;
}