package it.tdlight.telegrambackup.telegram;

import it.tdlight.ClientFactory;
import it.tdlight.Init;
import it.tdlight.Log;
import it.tdlight.Slf4JLogMessageHandler;
import it.tdlight.client.APIToken;
import it.tdlight.client.AuthenticationSupplier;
import it.tdlight.client.SimpleAuthenticationSupplier;
import it.tdlight.client.SimpleTelegramClientBuilder;
import it.tdlight.client.SimpleTelegramClientFactory;
import it.tdlight.client.TDLibSettings;
import it.tdlight.jni.TdApi;
import it.tdlight.jni.TdApi.Chat;
import it.tdlight.jni.TdApi.MessageSender;
import it.tdlight.jni.TdApi.Update;
import it.tdlight.jni.TdApi.UpdateAuthorizationState;
import it.tdlight.jni.TdApi.UpdateNewMessage;
import it.tdlight.telegrambackup.config.Configuration;
import it.tdlight.util.UnsupportedNativeLibraryException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TelegramSessionManager implements AutoCloseable {

	private Configuration configuration;
	private ClientFactory clientFactory;
	private SimpleTelegramClientFactory simpleClientFactory;

	public void initialize(Configuration configuration) throws UnsupportedNativeLibraryException {
		this.configuration = configuration;

		// Initialize TDLight native libraries
		Init.init();

		// Set the log level
		Log.setLogMessageHandler(1, new Slf4JLogMessageHandler());

		this.clientFactory = ClientFactory.create();
		this.simpleClientFactory = new SimpleTelegramClientFactory(clientFactory);
			// Obtain the API token
			//
			// var apiToken = new APIToken(your-api-id-here, "your-api-hash-here");
			//
			APIToken apiToken = APIToken.example();


			// Configure the client
			TDLibSettings settings = TDLibSettings.create(apiToken);

			// Configure the session directory.
			// After you authenticate into a session, the authentication will be skipped from the next restart!
			// If you want to ensure to match the authentication supplier user/bot with your session user/bot,
			//   you can name your session directory after your user id, for example: "tdlib-session-id12345"
			Path sessionPath = Paths.get("example-tdlight-session");
			settings.setDatabaseDirectoryPath(sessionPath.resolve("data"));
			settings.setDownloadedFilesDirectoryPath(sessionPath.resolve("downloads"));

			// Prepare a new client builder
			SimpleTelegramClientBuilder clientBuilder = simpleClientFactory.builder(settings);

			// Configure the authentication info
			// Replace with AuthenticationSupplier.consoleLogin(), or .user(xxx), or .bot(xxx);
			SimpleAuthenticationSupplier<?> authenticationData = AuthenticationSupplier.testUser(7381);
			// This is an example, remove this line to use the real telegram datacenters!
			settings.setUseTestDatacenter(true);


		// Add an example update handler that prints when the bot is started
		clientBuilder.addUpdateHandler(TdApi.UpdateAuthorizationState.class, this::onUpdateAuthorizationState);

		// Add an example command handler that stops the bot
		clientBuilder.addCommandHandler("stop", this::onStopCommand);

		// Add an example update handler that prints every received message
		clientBuilder.addUpdateHandler(TdApi.UpdateNewMessage.class, this::onUpdateNewMessage);
		clientBuilder.addUpdatesHandler(this::onUpdate);
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
		this.simpleClientFactory.close();
		this.clientFactory.close();
	}
}
