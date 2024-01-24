package net.rupyber_studios.police_terminal.webserver;

import net.rupyber_studios.police_terminal.PoliceTerminal;
import net.rupyber_studios.police_terminal.config.ModConfig;

import java.nio.file.Path;

public class Test {
    public static void main(String[] args) {
        ModConfig.INSTANCE = new ModConfig();
        ModConfig.INSTANCE.port = 3000;
        ModConfig.INSTANCE.https = true;
        ModConfig.INSTANCE.httpsCertificate = "/home/pyrix25633/keystore.jks";
        ModConfig.INSTANCE.httpsPassword = "a1b2c3";
        PoliceTerminal.startServer(Path.of("./run/saves/New World"));
    }
}