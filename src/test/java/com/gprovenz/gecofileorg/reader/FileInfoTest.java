package com.gprovenz.gecofileorg.reader;

import com.gprovenz.gecofileorg.settings.Settings;
import com.gprovenz.gecofileorg.settings.SettingsReader;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class FileInfoTest {

    @Test
    void givenSamePictureThenEqual() throws IOException {
        ClassLoader classLoader = new FileInfoTest().getClass().getClassLoader();
        Settings settings = SettingsReader.read(new File(classLoader.getResource("conf/test-copy-1.json").getFile()));

        File file1 = new File(classLoader.getResource("pictures/IMG_1.JPG").getFile());
        File file2 = new File(classLoader.getResource("pictures/IMG_1.JPG").getFile());

        FileInfo f1 = FileInfo.getInstance(settings, file1).get();
        FileInfo f2 = FileInfo.getInstance(settings, file2).get();

        assertTrue(f1!=f2);
        assertEquals(f1, f2);

        System.out.println(f1);
        System.out.println(f2);
    }

    @Test
    void givenDifferentPictureThenNotEqual() throws IOException {
        ClassLoader classLoader = new FileInfoTest().getClass().getClassLoader();
        Settings settings = SettingsReader.read(new File(classLoader.getResource("conf/test-copy-1.json").getFile()));

        File file1 = new File(classLoader.getResource("pictures/IMG_2.JPG").getFile());
        File file2 = new File(classLoader.getResource("pictures/IMG_3.JPG").getFile());

        FileInfo f1 = FileInfo.getInstance(settings, file1).get();
        FileInfo f2 = FileInfo.getInstance(settings, file2).get();

        assertTrue(f1!=f2);
        assertNotEquals(f1, f2);

        System.out.println(f1);
        System.out.println(f2);
    }
}