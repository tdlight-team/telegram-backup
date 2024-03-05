package it.tdlight.telegrambackup;

import it.tdlight.telegrambackup.config.Configuration.UserSettings;
import it.tdlight.telegrambackup.config.ConfigurationException;
import it.tdlight.telegrambackup.config.ConfigurationManager;
import it.tdlight.telegrambackup.telegram.TelegramSessionManager;
import it.tdlight.util.UnsupportedNativeLibraryException;
import java.io.Closeable;
import java.io.IOException;
import java.util.logging.Logger;

public class TelegramBackup implements Closeable {

	private static final Logger LOG = Logger.getLogger("TelegramBackup");
	private final ConfigurationManager configurationManager;
	private final TelegramSessionManager sessionManager;

	public static void main(String[] args) throws Exception {
		var backup = new TelegramBackup();
		backup.run();
	}

	public TelegramBackup() {
		this.configurationManager = new ConfigurationManager();
		this.sessionManager = new TelegramSessionManager();
	}

	public void run() throws Exception {
		configurationManager.initialize();

		sessionManager.initialize(configurationManager.getConfig());
	}

	@Override
	public void close() {
		this.sessionManager.close();
	}
}
