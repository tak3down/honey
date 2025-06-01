package io.github.honey;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public final class HoneyConfig {

  @JsonIgnore private final ObjectMapper jsonMapper;

  public String host;
  public int port;

  public boolean useSsl;
  public boolean enforceSsl;
  public int sslPort;

  public String forwardedIp;

  public Map<String, String> countryFlags = new HashMap<>();

  public HoneyConfig() {
    jsonMapper = new JsonMapper().enable(SerializationFeature.INDENT_OUTPUT);

    defaultValues();
  }

  public void load() {
    final Path dataPath = Paths.get("");
    final File configFile = dataPath.resolve("config.json").toFile();
    if (!configFile.exists()) {
      try {
        if (!configFile.createNewFile()) {
          throw new IOException("Failed to create config file");
        }

        jsonMapper.writeValue(configFile, this);
        return;

      } catch (final Exception exception) {
        throw new HoneyConfigException("Failed to create config file", exception);
      }
    }

    try {
      jsonMapper.readerForUpdating(this).readValue(configFile);
    } catch (final IOException exception) {
      throw new HoneyConfigException(
          "Failed to load config, because of unexpected exception", exception);
    }
  }

  private void defaultValues() {
    host = "0.0.0.0";
    port = 80;

    useSsl = false;
    enforceSsl = false;
    sslPort = 443;

    // CF-Connecting-IP OR X-Real-IP
    forwardedIp = "X-Real-IP";

    countryFlags.put("Stany Zjednoczone", "https://flagcdn.com/w320/us.png");
    countryFlags.put("Wielka Brytania", "https://flagcdn.com/w320/gb.png");
    countryFlags.put("Niemcy", "https://flagcdn.com/w320/de.png");
    countryFlags.put("Francja", "https://flagcdn.com/w320/fr.png");
    countryFlags.put("Włochy", "https://flagcdn.com/w320/it.png");
    countryFlags.put("Hiszpania", "https://flagcdn.com/w320/es.png");
    countryFlags.put("Kanada", "https://flagcdn.com/w320/ca.png");
    countryFlags.put("Australia", "https://flagcdn.com/w320/au.png");
    countryFlags.put("Japonia", "https://flagcdn.com/w320/jp.png");
    countryFlags.put("Chiny", "https://flagcdn.com/w320/cn.png");
    countryFlags.put("Brazylia", "https://flagcdn.com/w320/br.png");
    countryFlags.put("Indie", "https://flagcdn.com/w320/in.png");
    countryFlags.put("Rosja", "https://flagcdn.com/w320/ru.png");
    countryFlags.put("Meksyk", "https://flagcdn.com/w320/mx.png");
    countryFlags.put("Polska", "https://flagcdn.com/w320/pl.png");
    countryFlags.put("Szwecja", "https://flagcdn.com/w320/se.png");
    countryFlags.put("Norwegia", "https://flagcdn.com/w320/no.png");
    countryFlags.put("Holandia", "https://flagcdn.com/w320/nl.png");
    countryFlags.put("Belgia", "https://flagcdn.com/w320/be.png");
    countryFlags.put("Szwajcaria", "https://flagcdn.com/w320/ch.png");
    countryFlags.put("Austria", "https://flagcdn.com/w320/at.png");
    countryFlags.put("Portugalia", "https://flagcdn.com/w320/pt.png");
    countryFlags.put("Grecja", "https://flagcdn.com/w320/gr.png");
    countryFlags.put("Turcja", "https://flagcdn.com/w320/tr.png");
    countryFlags.put("Egipt", "https://flagcdn.com/w320/eg.png");
    countryFlags.put("RPA", "https://flagcdn.com/w320/za.png");
    countryFlags.put("Argentyna", "https://flagcdn.com/w320/ar.png");
    countryFlags.put("Chile", "https://flagcdn.com/w320/cl.png");
    countryFlags.put("Kolumbia", "https://flagcdn.com/w320/co.png");
    countryFlags.put("Korea Południowa", "https://flagcdn.com/w320/kr.png");
  }
}
