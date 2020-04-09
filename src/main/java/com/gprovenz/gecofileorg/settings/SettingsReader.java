package com.gprovenz.gecofileorg.settings;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class SettingsReader {
    private SettingsReader() { }

    public static Settings read(File f) throws IOException {
        Settings settings = new ObjectMapper().readValue(f, Settings.class);
        return settings;
    }
}
