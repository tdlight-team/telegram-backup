package it.tdlight.telegrambackup.config;

public class Configuration {
	public UserSettings[] users;

	public static class UserSettings {
		public String alias;
		public long id;
		public String phoneNumber;
		public String botToken;

		public void validate() throws ConfigurationException {

			if ((phoneNumber != null) == (botToken != null)) {
				throw new ConfigurationException("Please set either a bot token or a phone number");
			}
		}

		public boolean isBot() {
			return botToken != null;
		}

		public boolean isPhoneNumber() {
			return phoneNumber != null;
		}
	}

	public void validate() throws ConfigurationException {
		for (UserSettings user : users) {
			user.validate();
		}
	}
}

