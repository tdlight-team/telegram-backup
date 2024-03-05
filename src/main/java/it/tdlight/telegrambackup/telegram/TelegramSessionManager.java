package it.tdlight.telegrambackup.telegram;

import it.tdlight.ClientFactory;
import it.tdlight.Init;
import it.tdlight.Log;
import it.tdlight.Slf4JLogMessageHandler;
import it.tdlight.client.APIToken;
import it.tdlight.client.AuthenticationSupplier;
import it.tdlight.client.SimpleAuthenticationSupplier;
import it.tdlight.client.SimpleTelegramClient;
import it.tdlight.client.SimpleTelegramClientBuilder;
import it.tdlight.client.SimpleTelegramClientFactory;
import it.tdlight.client.TDLibSettings;
import it.tdlight.jni.TdApi.Chat;
import it.tdlight.jni.TdApi.MessageSender;
import it.tdlight.jni.TdApi.Update;
import it.tdlight.jni.TdApi.UpdateAuthorizationState;
import it.tdlight.jni.TdApi.UpdateNewMessage;
import it.tdlight.telegrambackup.config.Configuration;
import it.tdlight.telegrambackup.config.Configuration.UserSettings;
import it.tdlight.util.UnsupportedNativeLibraryException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class TelegramSessionManager implements AutoCloseable {

	private Configuration configuration;
	private ClientFactory clientFactory;
	private SimpleTelegramClientFactory simpleClientFactory;
	private List<SimpleTelegramClient> clients;

	public void initialize(Configuration configuration) throws UnsupportedNativeLibraryException, IOException {
		this.configuration = configuration;

		// Initialize TDLight native libraries
		Init.init();

		// Set the log level
		Log.setLogMessageHandler(1, new Slf4JLogMessageHandler());

		this.clientFactory = ClientFactory.create();
		this.simpleClientFactory = new SimpleTelegramClientFactory(clientFactory);
			// Obtain the API token
		var apiToken = new APIToken(configuration.apiId, configuration.apiHash);

		Path sessionsPath = Paths.get(configuration.sessionsPath);
		// Create base sessions directory
		if (!Files.notExists(sessionsPath)) {
			Files.createDirectories(sessionsPath);
		}
		// Create per-user session directory
		for (UserSettings user : configuration.users) {
			Path sessionPath = getSessionPath(sessionsPath, user);
			if (Files.notExists(sessionPath)) {
				Files.createDirectory(sessionPath);
			}
		}

		this.clients = Arrays.stream(configuration.users).map(user -> {
			Path sessionPath = getSessionPath(sessionsPath, user);

			// Configure the client
			TDLibSettings settings = TDLibSettings.create(apiToken);
			settings.setDatabaseDirectoryPath(sessionPath.resolve("data"));
			settings.setDownloadedFilesDirectoryPath(sessionPath.resolve("downloads"));

			// Prepare a new client builder
			SimpleTelegramClientBuilder clientBuilder = simpleClientFactory.builder(settings);

			// Configure the authentication info
			SimpleAuthenticationSupplier<?> authenticationData;
			if (user.isBot()) {
				authenticationData = AuthenticationSupplier.bot(user.botToken);
			} else {
				authenticationData = AuthenticationSupplier.user(user.phoneNumber);
			}

			// Add an example update handler that prints when the bot is started
			clientBuilder.addUpdateHandler(UpdateAuthorizationState.class, this::onUpdateAuthorizationState);

			// Add an example command handler that stops the bot
			clientBuilder.addCommandHandler("stop", this::onStopCommand);

			// Add an example update handler that prints every received message
			clientBuilder.addUpdateHandler(UpdateNewMessage.class, this::onUpdateNewMessage);
			clientBuilder.addUpdatesHandler(this::onUpdate);

			// Build the client
			return clientBuilder.build(authenticationData);
		}).toList();

	}

	private Path getSessionPath(Path sessionsPath, UserSettings user) {
		return sessionsPath.resolve(user.alias);
	}

	private void onUpdate(Update update) {

	}

	private void onUpdateNewMessage(UpdateNewMessage updateNewMessage) {

	}

	private void onStopCommand(Chat chat, MessageSender messageSender, String s) {

	}

	private void onUpdateAuthorizationState(UpdateAuthorizationState updateAuthorizationState) {

	}

	@Override
	public void close() {
		// Send close to all clients
		for (SimpleTelegramClient client : clients) {
			client.sendClose();
		}
		// Wait for exit
		for (SimpleTelegramClient client : clients) {
			try {
				client.closeAndWait();
			} catch (InterruptedException ignored) {
			}
		}
		this.simpleClientFactory.close();
		this.clientFactory.close();
	}
}
