package net.rupyber_studios.police_terminal.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import net.rupyber_studios.police_terminal.client.HudPosition;

@Config(name = "police_terminal")
public class ModConfig implements ConfigData {
    @ConfigEntry.Gui.Excluded
    public static ModConfig INSTANCE;

    public static void init() {
        AutoConfig.register(ModConfig.class, JanksonConfigSerializer::new);
        INSTANCE = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
    }

    // default

    @ConfigEntry.Gui.Tooltip()
    @ConfigEntry.Category("default")
    @Comment("The horizontal position of the hud")
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    public HudPosition.Horizontal hudHorizontal = HudPosition.Horizontal.LEFT;

    @ConfigEntry.Gui.Tooltip()
    @ConfigEntry.Category("default")
    @Comment("The vertical position of the hud")
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    public HudPosition.Vertical hudVertical = HudPosition.Vertical.TOP;

    // web

    @ConfigEntry.Gui.Tooltip()
    @ConfigEntry.Category("web")
    @Comment("The port used to connect to the web terminal (<address>:<port>)")
    public int port = 3000;

    @ConfigEntry.Gui.Tooltip()
    @ConfigEntry.Category("web")
    @Comment("If enabled the server will use the https protocol (strongly recommended) and the certificate path below should be set")
    public boolean https = false;

    @ConfigEntry.Gui.Tooltip()
    @ConfigEntry.Category("web")
    @Comment("The https certificate path, if invalid the server will default to http")
    public String httpsCertificate = "";

    @ConfigEntry.Gui.Tooltip()
    @ConfigEntry.Category("web")
    @Comment("The https certificate password, if invalid the server will default to http")
    public String httpsPassword = "";
}