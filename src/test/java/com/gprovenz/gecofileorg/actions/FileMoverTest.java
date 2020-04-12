package com.gprovenz.gecofileorg.actions;

import com.gprovenz.gecofileorg.reader.FileTools;
import com.gprovenz.gecofileorg.settings.Settings;
import com.gprovenz.gecofileorg.settings.SettingsReader;
import com.gprovenz.gecofileorg.utils.DirectoryTree;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.io.FileUtils.copyFileToDirectory;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileMoverTest {
    private static final List<String> files = new ArrayList<>();
    private File tempDir;
    private File sourceDir;
    private File destDir;
    private static Logger logger = LogManager.getLogger();
    private static ClassLoader classLoader = new FileMoverTest().getClass().getClassLoader();
    private Settings settings;

    @BeforeAll
    public static void init() {
        files.clear();
        for (int i=1;i<=5; i++) {
            files.add(String.format("pictures/IMG_%s.JPG", i));
        }
        for (int i=1;i<=2; i++) {
            files.add(String.format("other/doc_%s.txt", i));
        }
    }

    @BeforeEach
    public void initEachTest() throws IOException {
        tempDir = Files.createTempDirectory("testCopy").toFile();
        logger.info("Created temp directory: {}", tempDir);

        sourceDir = new File(tempDir, "source");
        sourceDir.mkdirs();
        destDir = new File(tempDir, "dest");

        for (String file:files) {
            File srcFile = new File(classLoader.getResource(file).getFile());
            logger.info("Copying test file {} to {}", srcFile, sourceDir);
            copyFileToDirectory(srcFile, sourceDir);
        }
        logger.info("Test files copied to test dir {} files: {}", sourceDir, files);

        File settingsFile = new File(classLoader.getResource("conf/test-move-1.json").getFile());
        settings = SettingsReader.read(settingsFile);
        settings.setSourcePath(tempDir.getAbsolutePath());

        settings.setDestinationPath(destDir.getAbsolutePath());
        logger.info("Test settings file read: {}", settingsFile);
    }

    @AfterEach
    public void cleanEachTest() throws IOException {
        FileUtils.deleteDirectory(tempDir);
        logger.info("Deleted temp directory: {}", tempDir);
    }

    @Test
    public void givenUniqueFilesThenMoved() throws IOException {
        FileMover mover = new FileMover(settings);
        mover.moveAllFiles();
        assertTrue(destDir.exists());

        assertTrue(new File(destDir, "Photo").exists());

        String tree = DirectoryTree.getTree(tempDir);

        assertEquals("|  |  +--" + tempDir.getName() + "\n" +
                "|  |  |  +--dest\n" +
                "|  |  |  |  +--Photo\n" +
                "|  |  |  |  |  +--2017\n" +
                "|  |  |  |  |  |  +--08-August-2017\n" +
                "|  |  |  |  |  |  |  +--11-Aug-2017\n" +
                "|  |  |  |  |  |  |  |  +--IMG_2.JPG\n" +
                "|  |  |  |  |  |  |  +--15-Aug-2017\n" +
                "|  |  |  |  |  |  |  |  +--IMG_1.JPG\n" +
                "|  |  |  |  |  |  |  |  +--IMG_3.JPG\n" +
                "|  |  |  |  |  +--2018\n" +
                "|  |  |  |  |  |  +--05-May-2018\n" +
                "|  |  |  |  |  |  |  +--26-May-2018\n" +
                "|  |  |  |  |  |  |  |  +--IMG_4.JPG\n" +
                "|  |  |  |  |  |  |  |  +--IMG_5.JPG\n" +
                "|  |  |  +--source\n" +
                "|  |  |  |  +--doc_1.txt\n" +
                "|  |  |  |  +--doc_2.txt\n", tree);


    }

    @Test
    public void givenDuplicateFilesThenMovedWithNoDuplicates() throws IOException, InterruptedException {
        // create 2 duplicate files
        File subfolder = new File(sourceDir, "duplicates");
        subfolder.mkdirs();
        FileUtils.copyFileToDirectory(new File(sourceDir, "IMG_1.JPG"), subfolder);
        FileUtils.copyFileToDirectory(new File(sourceDir, "IMG_2.JPG"), subfolder);

        FileMover mover = new FileMover(settings);
        mover.moveAllFiles();
        assertTrue(destDir.exists());

        assertTrue(new File(destDir, "Photo").exists());

        String tree = DirectoryTree.getTree(tempDir);

        assertEquals("|  |  +--" + tempDir.getName() + "\n" +
                "|  |  |  +--dest\n" +
                "|  |  |  |  +--Photo\n" +
                "|  |  |  |  |  +--2017\n" +
                "|  |  |  |  |  |  +--08-August-2017\n" +
                "|  |  |  |  |  |  |  +--11-Aug-2017\n" +
                "|  |  |  |  |  |  |  |  +--IMG_2.JPG\n" +
                "|  |  |  |  |  |  |  +--15-Aug-2017\n" +
                "|  |  |  |  |  |  |  |  +--IMG_1.JPG\n" +
                "|  |  |  |  |  |  |  |  +--IMG_3.JPG\n" +
                "|  |  |  |  |  +--2018\n" +
                "|  |  |  |  |  |  +--05-May-2018\n" +
                "|  |  |  |  |  |  |  +--26-May-2018\n" +
                "|  |  |  |  |  |  |  |  +--IMG_4.JPG\n" +
                "|  |  |  |  |  |  |  |  +--IMG_5.JPG\n" +
                "|  |  |  +--source\n" +
                "|  |  |  |  +--doc_1.txt\n" +
                "|  |  |  |  +--doc_2.txt\n", tree);


    }
}