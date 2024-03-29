package net.rupyber_studios.police_terminal;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.rupyber_studios.police_terminal.client.HudOverlay;
import net.rupyber_studios.police_terminal.networking.ModMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PoliceTerminalClient implements ClientModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger(PoliceTerminal.MOD_ID);

	@Override
	public void onInitializeClient() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		ModMessages.registerS2CPackets();

		HudRenderCallback.EVENT.register(new HudOverlay());

		LOGGER.info("Initializing client");
	}
}