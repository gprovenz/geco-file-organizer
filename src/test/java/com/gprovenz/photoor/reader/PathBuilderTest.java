package com.gprovenz.photoor.reader;

import com.gprovenz.photoor.settings.Settings;
import com.gprovenz.photoor.settings.SettingsReader;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PathBuilderTest {

    @Test
    void givenFileInfoThenCorrectPath() throws IOException {
        ClassLoader classLoader = new FileInfoTest().getClass().getClassLoader();

        Settings settings = SettingsReader.read(new File(classLoader.getResource("conf/test-copy-1.json").getFile()));
        FileInfo fileInfo = FileInfo.getInstance(new File(classLoader.getResource("pictures/IMG_7546.JPG").getFile()));

        File root = new File ("/temp/outpath/");
        String outPath = PathBuilder.buildDestPath(settings, root, fileInfo).getPath().replace("\\", "/");

        assertEquals("/temp/outpath/PICTURE/2018/05-maggio-2018/26-mag-2018/IMG_7546.JPG", outPath);
        System.out.println(settings);
        System.out.println(outPath);
    }
}