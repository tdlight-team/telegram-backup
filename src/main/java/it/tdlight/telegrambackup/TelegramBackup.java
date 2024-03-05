package it.tdlight.telegrambackup;

import it.tdlight.telegrambackup.config.ConfigurationException;
import it.tdlight.telegrambackup.config.ConfigurationManager;
import java.util.logging.Logger;

public class TelegramBackup {

	private static final Logger LOG = Logger.getLogger("TelegramBackup");
	private final ConfigurationManager configurationManager;

	public static void main(String[] args) throws ConfigurationException {
		var backup = new TelegramBackup();
		backup.run();
	}

	public TelegramBackup() {
		this.configurationManager = new ConfigurationManager();
	}

	public void run() throws ConfigurationException {
		configurationManager.initialize();
	}
}
