package com.gprovenz.gecofileorg.reader;

import com.gprovenz.gecofileorg.settings.Settings;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Optional;

public class FileTools {
    private static Logger logger = LogManager.getLogger();

    public static boolean isToIgnore(Optional<FileInfo> fileInfo) {
        return !fileInfo.isPresent() ||
                !fileInfo.get().getFileType().isPresent() ||
                fileInfo.get().getFileType().get().isIgnore();
    }

    public static void removeEmptyFolders(String folderPath) throws IOException {
        logger.info("Removing empty folders...");

        // remove empty dirs
        Files.walk(Paths.get(folderPath))
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .filter(f -> f.isDirectory())
                .forEach(e -> deleteEmptyDir(e));
    }

    private static boolean deleteEmptyDir(File dir) {
        if (!dir.isDirectory()) {
            throw new IllegalArgumentException("Can delete only folders");
        }
        if (dir.listFiles().length>0) {
            return false;
        }
        return dir.delete();
    }

    public static boolean sameContent(Settings settings, File sourceFile, File destFile) throws IOException {
        Optional<FileInfo> source = FileInfo.getInstance(settings, sourceFile);
        Optional<FileInfo> dest = FileInfo.getInstance(settings, destFile);
        if(source.isPresent() && dest.isPresent() && source.get().potentiallySameFile(dest.get())) {
            // they seem the same file. Checking for content:
            return FileUtils.contentEquals(sourceFile, destFile);
        } else {
            return false;
        }
    }
}