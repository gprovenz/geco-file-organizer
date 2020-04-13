package com.gprovenz.gecofileorg.reader;

import com.gprovenz.gecofileorg.settings.Settings;
import com.gprovenz.gecofileorg.settings.SettingsReader;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PathBuilderTest {

    @Test
    void givenFileInfoThenCorrectPath() throws IOException {
        ClassLoader classLoader = FileInfoTest.class.getClassLoader();

        Settings settings = SettingsReader.read(new File(Objects.requireNonNull(classLoader.getResource("conf/test-copy-1.json")).getFile()));
        Optional<FileInfo> fileInfo = FileInfo.getInstance(settings, new File(Objects.requireNonNull(classLoader.getResource("pictures/IMG_2.JPG")).getFile()));

        assertTrue(fileInfo.isPresent());

        File root = new File ("/temp/outpath/");
        String outPath = PathBuilder.buildDestPath(settings, root, fileInfo.get()).getPath().replace("\\", "/");

        assertEquals("/temp/outpath/Photos/2017/08-August-2017/11-Aug-2017/IMG_2.JPG", outPath);
    }

    @Test
    void givenNotMatchingFileThenOther() throws IOException {
        ClassLoader classLoader = FileInfoTest.class.getClassLoader();

        Settings settings = SettingsReader.read(new File(Objects.requireNonNull(classLoader.getResource("conf/test-copy-1.json")).getFile()));
        Optional<FileInfo> fileInfo = FileInfo.getInstance(settings, new File(Objects.requireNonNull(classLoader.getResource("pictures/IMG_2.JPG")).getFile()));

        assertTrue(fileInfo.isPresent());

        File root = new File ("/temp/outpath/");
        String outPath = PathBuilder.buildDestPath(settings, root, fileInfo.get()).getPath().replace("\\", "/");

        assertEquals("/temp/outpath/Photos/2017/08-August-2017/11-Aug-2017/IMG_2.JPG", outPath);
    }
}