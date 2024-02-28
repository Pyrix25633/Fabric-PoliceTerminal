package net.rupyber_studios.police_terminal;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.util.WorldSavePath;
import net.rupyber_studios.police_terminal.command.argument.OnlineCallsignArgumentType;
import net.rupyber_studios.police_terminal.command.argument.RankArgumentType;
import net.rupyber_studios.police_terminal.config.ModConfig;
import net.rupyber_studios.police_terminal.networking.packet.SyncPlayerInfoS2CPacket;
import net.rupyber_studios.police_terminal.networking.packet.SyncRanksS2CPacket;
import net.rupyber_studios.police_terminal.util.ModRegistries;
import net.rupyber_studios.police_terminal.webserver.WebServer;
import net.rupyber_studios.rupyber_database_api.RupyberDatabaseAPI;
import net.rupyber_studios.rupyber_database_api.table.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.FileInputStream;
import java.net.ServerSocket;
import java.nio.file.Path;
import java.security.KeyStore;
import java.sql.Connection;
import java.sql.SQLException;

public class PoliceTerminal implements ModInitializer {
	public static final String MOD_ID = "police_terminal";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static Connection connection;
	public static ServerSocket socket;
	public static Thread serverThread;

	@Override
	public void onInitialize() {

		ModConfig.init();
		RupyberDatabaseAPI.setPoliceTerminalConfig(ModConfig.INSTANCE);

		ModRegistries.registerCommands();

		ServerLifecycleEvents.SERVER_STARTING.register((server) -> startServer(server.getSavePath(WorldSavePath.ROOT)));

		ServerLifecycleEvents.SERVER_STOPPED.register((server) -> stopServer());

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			try {
				SyncRanksS2CPacket.send(handler.player);
				Player.insertOrUpdate(handler.player.getUuid(), handler.player.getGameProfile().getName());
				SyncPlayerInfoS2CPacket.send(handler.player);
				OnlineCallsignArgumentType.init();
			} catch (SQLException e) {
				LOGGER.error("Error handling player join: ", e);
			}
		});

		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
			try {
				Player.handleDisconnection(handler.player.getUuid());
				OnlineCallsignArgumentType.init();
			} catch (SQLException e) {
				LOGGER.error("Error handling player disconnection: ", e);
			}
		});

		LOGGER.info("Initializing main");
	}

	public static void startServer(Path worldPath) {
		RupyberDatabaseAPI.connectIfNotConnected(worldPath);
		RupyberDatabaseAPI.startPoliceTerminal();
		RankArgumentType.init();
		OnlineCallsignArgumentType.init();

		try {
			boolean httpsError = false;
			if(ModConfig.INSTANCE.https)
				httpsError = !startHttpsServer();
			if(!ModConfig.INSTANCE.https || httpsError)
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
	}

	public static boolean startHttpsServer() {
		try {
			KeyStore keyStore = KeyStore.getInstance("JKS");
			keyStore.load(new FileInputStream(ModConfig.INSTANCE.httpsCertificate),
					ModConfig.INSTANCE.httpsPassword.toCharArray());
			KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
			keyManagerFactory.init(keyStore, ModConfig.INSTANCE.httpsPassword.toCharArray());
			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(keyManagerFactory.getKeyManagers(), null, null);
			socket = sslContext.getServerSocketFactory().createServerSocket(ModConfig.INSTANCE.port);
			LOGGER.info("Successfully initialized ServerSocket with https");
			return true;
		} catch(Exception e) {
			LOGGER.warn("Error starting https server, defaulting to http: ", e);
		}
		return false;
	}

	public static void stopServer() {
		RupyberDatabaseAPI.startPoliceTerminal();
		try {
			LOGGER.info("Closing server socket");
			socket.close();
			serverThread.join(0, 1);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}