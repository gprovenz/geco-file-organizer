package com.gprovenz.photoor.reader;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class FileInfoTest {

    @Test
    void givenSamePictureThenEqual() throws IOException {
        ClassLoader classLoader = new FileInfoTest().getClass().getClassLoader();
        File file1 = new File(classLoader.getResource("pictures/IMG_7546.JPG").getFile());
        File file2 = new File(classLoader.getResource("pictures/IMG_7546.JPG").getFile());

        FileInfo f1 = FileInfo.getInstance(file1);
        FileInfo f2 = FileInfo.getInstance(file2);

        assertTrue(f1!=f2);
        assertEquals(f1, f2);

        System.out.println(f1);
        System.out.println(f2);
    }

    @Test
    void givenDifferentPictureThenNotEqual() throws IOException {
        ClassLoader classLoader = new FileInfoTest().getClass().getClassLoader();
        File file1 = new File(classLoader.getResource("pictures/IMG_7546.JPG").getFile());
        File file2 = new File(classLoader.getResource("pictures/IMG_7547.JPG").getFile());

        FileInfo f1 = FileInfo.getInstance(file1);
        FileInfo f2 = FileInfo.getInstance(file2);

        assertTrue(f1!=f2);
        assertNotEquals(f1, f2);

        System.out.println(f1);
        System.out.println(f2);
    }
}