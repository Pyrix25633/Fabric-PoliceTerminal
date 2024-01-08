package net.rupyber_studios.police_terminal.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import net.rupyber_studios.police_terminal.client.HudPosition;

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
    @ConfigEntry.BoundedDiscrete(min = 5, max = 35)
    public int hudHorizontalDistance = 10;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("default")
    @Comment("The hud vertical distance from the screen border")
    @ConfigEntry.BoundedDiscrete(min = 5, max = 35)
    public int hudVerticalDistance = 10;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("default")
    @Comment("The background color of the hud")
    @ConfigEntry.ColorPicker(allowAlpha = true)
    public int hudBackground = 0x55333333;

    // duty

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("duty")
    @Comment("The list of ranks used in the duty system, highers ones are higher in the chain of command")
    public List<Rank> ranks = List.of(
            new Rank("Officer", 0x5555FF),
            new Rank("Rookie", 0x55FFFF)
    );

    public static class Rank {
        public Rank() {}

        public Rank(String rank, int color) {
            this.rank = rank;
            this.color = color;
        }

        public String rank = "New Rank";

        @ConfigEntry.ColorPicker
        public int color = 0x5555FF;
    }

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

    // web

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("web")
    @Comment("The port used to connect to the web terminal (<address>:<port>)")
    public int port = 3000;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("web")
    @Comment("If enabled the server will use the https protocol (strongly recommended) and the certificate path below should be set")
    public boolean https = false;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("web")
    @Comment("The https certificate path, if invalid the server will default to http")
    public String httpsCertificate = "";

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("web")
    @Comment("The https certificate password, if invalid the server will default to http")
    public String httpsPassword = "";
}