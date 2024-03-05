package it.tdlight.telegrambackup;

import it.tdlight.jni.TdApi.User;

public class Settings {
	public UserSettings[] users;

	public static class UserSettings {
		public String alias;
		public long id;
		public String phoneNumber;
		public String botToken;

		public void validate() {
			if ((phoneNumber != null) == (botToken != null)) {
				throw new UnsupportedOperationException("Bot ");
			}
		}
	}

	public void validate() {
		for (UserSettings user : users) {
			user.validate();
		}
	}
}

