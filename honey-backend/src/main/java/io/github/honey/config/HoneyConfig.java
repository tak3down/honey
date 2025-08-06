package io.github.honey.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public final class HoneyConfig {

  private final @JsonIgnore ObjectMapper objectMapper;

  public String host;
  public int port;
  public String apiBase;
  public String forwardedIp;
  public Map<String, String> countryFlags;

  @Autowired
  HoneyConfig(final ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
    defaultValues();
  }

  @PostConstruct
  private void loadConfig() {
    final File configFile = Paths.get("").resolve("config.json").toFile();
    if (configFile.exists()) {
      try {
        objectMapper.readerForUpdating(this).readValue(configFile);
        return;
      } catch (final Exception exception) {
        throw new HoneyConfigException(
            "Failed to load config, because of unexpected exception", exception);
      }
    }

    try {
      if (!configFile.createNewFile()) {
        throw new IOException("Failed to create config file");
      }

      objectMapper.writeValue(configFile, this);
    } catch (final Exception exception) {
      throw new HoneyConfigException("Failed to create config file", exception);
    }
  }

  private void defaultValues() {
    host = "0.0.0.0";
    apiBase = "http://localhost:80";
    port = 80;

    // CF-Connecting-IP OR X-Real-IP
    forwardedIp = "X-Real-IP";

    countryFlags = new HashMap<>();
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
    countryFlags.put("Ukraina", "https://flagcdn.com/w320/ua.png");
    countryFlags.put("Finlandia", "https://flagcdn.com/w320/fi.png");
    countryFlags.put("Dania", "https://flagcdn.com/w320/dk.png");
    countryFlags.put("Irlandia", "https://flagcdn.com/w320/ie.png");
    countryFlags.put("Czechy", "https://flagcdn.com/w320/cz.png");
    countryFlags.put("Węgry", "https://flagcdn.com/w320/hu.png");
    countryFlags.put("Rumunia", "https://flagcdn.com/w320/ro.png");
    countryFlags.put("Bułgaria", "https://flagcdn.com/w320/bg.png");
    countryFlags.put("Serbia", "https://flagcdn.com/w320/rs.png");
    countryFlags.put("Chorwacja", "https://flagcdn.com/w320/hr.png");
    countryFlags.put("Słowacja", "https://flagcdn.com/w320/sk.png");
    countryFlags.put("Słowenia", "https://flagcdn.com/w320/si.png");
    countryFlags.put("Litwa", "https://flagcdn.com/w320/lt.png");
    countryFlags.put("Łotwa", "https://flagcdn.com/w320/lv.png");
    countryFlags.put("Estonia", "https://flagcdn.com/w320/ee.png");
    countryFlags.put("Arabia Saudyjska", "https://flagcdn.com/w320/sa.png");
    countryFlags.put("Zjednoczone Emiraty Arabskie", "https://flagcdn.com/w320/ae.png");
    countryFlags.put("Izrael", "https://flagcdn.com/w320/il.png");
    countryFlags.put("Iran", "https://flagcdn.com/w320/ir.png");
    countryFlags.put("Pakistan", "https://flagcdn.com/w320/pk.png");
    countryFlags.put("Tajlandia", "https://flagcdn.com/w320/th.png");
    countryFlags.put("Wietnam", "https://flagcdn.com/w320/vn.png");
    countryFlags.put("Indonezja", "https://flagcdn.com/w320/id.png");
    countryFlags.put("Malezja", "https://flagcdn.com/w320/my.png");
    countryFlags.put("Filipiny", "https://flagcdn.com/w320/ph.png");
    countryFlags.put("Bangladesz", "https://flagcdn.com/w320/bd.png");
    countryFlags.put("Nowa Zelandia", "https://flagcdn.com/w320/nz.png");
    countryFlags.put("Nigeria", "https://flagcdn.com/w320/ng.png");
    countryFlags.put("Kenia", "https://flagcdn.com/w320/ke.png");
    countryFlags.put("Maroko", "https://flagcdn.com/w320/ma.png");
    countryFlags.put("Tunezja", "https://flagcdn.com/w320/tn.png");
    countryFlags.put("Algieria", "https://flagcdn.com/w320/dz.png");
    countryFlags.put("Peru", "https://flagcdn.com/w320/pe.png");
    countryFlags.put("Wenezuela", "https://flagcdn.com/w320/ve.png");
    countryFlags.put("Ekwador", "https://flagcdn.com/w320/ec.png");
  }
}
