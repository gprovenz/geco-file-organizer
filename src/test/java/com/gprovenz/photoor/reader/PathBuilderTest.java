package com.gprovenz.photoor.reader;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

class PathBuilderTest {

    @Test
    void givenFileInfoThenCorrectPath() throws IOException {
        ClassLoader classLoader = new FileInfoTest().getClass().getClassLoader();
        File file = new File(classLoader.getResource("pictures/IMG_7546.JPG").getFile());

        FileInfo fileInfo = FileInfo.getInstance(file);

        File root = new File ("c:\\temp\\outpath\\");
        File outPath = PathBuilder.buildDestPath(root, fileInfo);

        System.out.println(outPath);
    }
}