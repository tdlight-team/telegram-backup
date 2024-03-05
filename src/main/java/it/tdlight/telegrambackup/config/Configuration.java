package it.tdlight.telegrambackup.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

public class Configuration {

	@JsonProperty(required = true)
	public UserSettings[] users;

	public static class UserSettings {
		@JsonProperty(required = true)
		public String alias;
		@JsonProperty("phone_number")
		public String phoneNumber;
		@JsonIgnore
		private transient PhoneNumber parsedPhoneNumber;
		@JsonProperty("bot_token")
		public String botToken;

		public void validate() throws ConfigurationException {
			if ((phoneNumber != null) == (botToken != null)) {
				throw new ConfigurationException("Please set either a bot token or a phone number");
			}
			if (phoneNumber != null) {
				try {
					parsedPhoneNumber = PhoneNumberUtil.getInstance().parse(phoneNumber, "001");
				} catch (NumberParseException e) {
					throw new ConfigurationException("Invalid phone number", e);
				}
			}
		}

		public boolean isBot() {
			return botToken != null;
		}

		public boolean isPhoneNumber() {
			return phoneNumber != null;
		}

		public PhoneNumber getPhoneNumber() {
			return parsedPhoneNumber;
		}
	}

	public void validate() throws ConfigurationException {
		for (UserSettings user : users) {
			user.validate();
		}
	}
}

