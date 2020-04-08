package com.gprovenz.photoor.reader;

import com.gprovenz.photoor.settings.Settings;
import com.gprovenz.photoor.settings.SettingsReader;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PathBuilderTest {

    @Test
    void givenFileInfoThenCorrectPath() throws IOException {
        ClassLoader classLoader = new FileInfoTest().getClass().getClassLoader();

        Locale.setDefault(Locale.US);

        Settings settings = SettingsReader.read(new File(classLoader.getResource("conf/test-copy-1.json").getFile()));
        Optional<FileInfo> fileInfo = FileInfo.getInstance(settings, new File(classLoader.getResource("pictures/IMG_5370.JPG").getFile()));

        assertTrue(fileInfo.isPresent());

        File root = new File ("/temp/outpath/");
        String outPath = PathBuilder.buildDestPath(settings, root, fileInfo.get()).getPath().replace("\\", "/");

        assertEquals("/temp/outpath/Photo/2017/08-August-2017/11-Aug-2017/IMG_5370.JPG", outPath);
        System.out.println(settings);
        System.out.println(outPath);
    }
}