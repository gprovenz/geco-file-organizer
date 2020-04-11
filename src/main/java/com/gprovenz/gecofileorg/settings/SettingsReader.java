package com.gprovenz.gecofileorg.settings;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

public class SettingsReader {
    private SettingsReader() { }

    public static Settings read(File f) throws IOException {
        Settings settings = new ObjectMapper().readValue(f, Settings.class);
        if (settings.getLocale()!=null) {
            Locale.setDefault(settings.getLocale());
        }
        return settings;
    }
}
