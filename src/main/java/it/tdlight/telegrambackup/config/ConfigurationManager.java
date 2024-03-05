package it.tdlight.telegrambackup.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigurationManager {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	private Configuration configuration;

	public void initialize() throws ConfigurationException {
		var currentConfigPath = Path.of("").resolve("telegram-backup.json");

		if (Files.notExists(currentConfigPath)) {
			var sampleCodeStream = ConfigurationManager.class.getResourceAsStream("/sample_code.json");
			assert sampleCodeStream != null;
			try {
				Files.copy(sampleCodeStream, currentConfigPath);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		try (var is = Files.newInputStream(currentConfigPath)) {
			this.configuration = load(is);
		} catch (IOException e) {
			throw new ConfigurationException("Cannot load configuration", e);
		}
	}


	private Configuration load(InputStream fileInput) throws ConfigurationException {
		String rawData;
		try {
			rawData = new String(fileInput.readAllBytes(), StandardCharsets.UTF_8);
		} catch (IOException ex) {
			throw new ConfigurationException("Cannot load configuration", ex);
		}
		Configuration configuration;
		try {
			configuration = OBJECT_MAPPER.readValue(rawData, Configuration.class);
		} catch (JsonProcessingException e) {
			throw new ConfigurationException("Corrupted configuration file", e);
		}
		configuration.validate();
		return configuration;
	}

	public Configuration getConfig() {
		return configuration;
	}
}
