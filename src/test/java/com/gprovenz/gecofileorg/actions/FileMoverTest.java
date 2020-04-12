package com.gprovenz.gecofileorg.actions;

import com.gprovenz.gecofileorg.CommandLineApp;
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
import java.util.Objects;

import static org.apache.commons.io.FileUtils.copyFileToDirectory;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileMoverTest {
    private static final List<String> files = new ArrayList<>();
    private static final Logger logger = LogManager.getLogger();
    private File tempDir;
    private File sourceDir;
    private File destDir;
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
        ClassLoader classLoader = FileMoverTest.class.getClassLoader();

        tempDir = Files.createTempDirectory("testCopy").toFile();
        logger.info("Created temp directory: {}", tempDir);

        sourceDir = new File(tempDir, "source");
        assertTrue(sourceDir.mkdirs());
        destDir = new File(tempDir, "dest");

        for (String file:files) {
            File srcFile = new File(Objects.requireNonNull(classLoader.getResource(file)).getFile());
            logger.info("Copying test file {} to {}", srcFile, sourceDir);
            copyFileToDirectory(srcFile, sourceDir);
        }
        logger.info("Test files copied to test dir {} files: {}", sourceDir, files);

        File settingsFile = new File(Objects.requireNonNull(classLoader.getResource("conf/test-move-1.json")).getFile());
        settings = SettingsReader.read(settingsFile);
        settings.setSourcePath(sourceDir.getAbsolutePath());

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
        CommandLineApp.execCommand(settings);
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
    public void givenDuplicateFilesThenMoved() throws IOException {
        // change settings to don't delete duplicates
        settings.setRemoveDuplicates(false);

        // create 2 duplicate files
        File subfolder = new File(sourceDir, "duplicates");
        assertTrue(subfolder.mkdirs());
        FileUtils.copyFileToDirectory(new File(sourceDir, "IMG_1.JPG"), subfolder);
        FileUtils.copyFileToDirectory(new File(sourceDir, "IMG_2.JPG"), subfolder);

        CommandLineApp.execCommand(settings);
        assertTrue(destDir.exists());
        assertTrue(new File(destDir, "Photo").exists());

        String tree = DirectoryTree.getTree(tempDir);

        assertTrue(tree.startsWith("|  |  +--" + tempDir.getName() + "\n" +
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
                "|  |  |  +--source\n"));

        int index = tree.indexOf("+--source");
        assertTrue(tree.indexOf("IMG_1.JPG", index)>0);
        assertTrue(tree.indexOf("IMG_2.JPG", index)>0);
        assertTrue(tree.indexOf("doc_1.txt", index)>0);
        assertTrue(tree.indexOf("doc_2.txt", index)>0);
    }

    @Test
    public void givenDuplicateFilesThenMovedWithNoDuplicates() throws IOException {
        // settings are configured to delete duplicates

        // create 2 duplicate files
        File subfolder = new File(sourceDir, "duplicates");
        assertTrue(subfolder.mkdirs());
        FileUtils.copyFileToDirectory(new File(sourceDir, "IMG_1.JPG"), subfolder);
        FileUtils.copyFileToDirectory(new File(sourceDir, "IMG_2.JPG"), subfolder);

        CommandLineApp.execCommand(settings);
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
    public void givenSourceFolderMatchDestinationFolderThenOrganized() throws IOException {
        // settings are configured to delete duplicates
        settings.setDestinationPath(settings.getSourcePath());

        // create 2 duplicate files
        File subfolder = new File(sourceDir, "duplicates");
        assertTrue(subfolder.mkdirs());
        FileUtils.copyFileToDirectory(new File(sourceDir, "IMG_1.JPG"), subfolder);
        FileUtils.copyFileToDirectory(new File(sourceDir, "IMG_2.JPG"), subfolder);

        CommandLineApp.execCommand(settings);

        String tree = DirectoryTree.getTree(tempDir);

        assertEquals("|  |  +--" + tempDir.getName() + "\n" +
                "|  |  |  +--source\n" +
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
                "|  |  |  |  +--doc_1.txt\n" +
                "|  |  |  |  +--doc_2.txt\n", tree);
    }
}