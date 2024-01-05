package net.rupyber_studios.police_terminal;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.util.WorldSavePath;
import net.rupyber_studios.police_terminal.config.ModConfig;
import net.rupyber_studios.police_terminal.database.DatabaseManager;
import net.rupyber_studios.police_terminal.webserver.WebServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.ServerSocket;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PoliceTerminal implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final String MOD_ID = "police_terminal";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static Connection connection;
	public static ServerSocket socket;
	public static Thread serverThread;

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		ModConfig.init();

		ServerLifecycleEvents.SERVER_STARTING.register(server -> {
			Path worldPath = server.getSavePath(WorldSavePath.ROOT);
			String url = "jdbc:sqlite:" + worldPath + "police.db";

			try {
				connection = DriverManager.getConnection(url);
				if(connection != null) {
					LOGGER.info("Connected to the database");
					DatabaseManager.createTables();
				}
				else throw new IllegalStateException("Not connected to the police database!");
			} catch(Exception e) {
				LOGGER.error("Error: ", e);
				throw new IllegalStateException("Error operating the police database!");
			}

			try {
				socket = new ServerSocket(ModConfig.INSTANCE.port);
				serverThread = new Thread(() -> {
					while(!socket.isClosed()) {
						try {
							WebServer.handleRequest(socket.accept());
						} catch (Exception e) {
							LOGGER.warn("Error handling request: ", e);
						}
					}
				});
				serverThread.start();
				LOGGER.info("Socket listening on port " + ModConfig.INSTANCE.port);
			} catch(Exception e) {
				LOGGER.error("Error: ", e);
				throw new IllegalStateException("Error operating the server socket!");
			}
		});

		ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
			try {
				LOGGER.info("Closing police database connection");
				connection.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}

			try {
				LOGGER.info("Closing server socket");
				socket.close();
				serverThread.join(0, 1);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});

		LOGGER.info("Initializing main");
	}
}