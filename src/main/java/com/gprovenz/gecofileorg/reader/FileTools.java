package com.gprovenz.gecofileorg.reader;

import com.gprovenz.gecofileorg.settings.Settings;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;

public class FileTools {
    private static final Logger logger = LoggerFactory.getLogger(FileTools.class);

    public static boolean isToIgnore(FileInfo fileInfo) {
        return  !fileInfo.getFileType().isPresent() ||
                fileInfo.getFileType().get().isIgnore();
    }

    public static void removeEmptyFolders(String folderPath) throws IOException {
        logger.info("Removing empty folders...");

        // remove empty dirs
        Files.walk(Paths.get(folderPath))
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .filter(File::isDirectory)
                .forEach(FileTools::deleteEmptyDir);
    }

    private static boolean deleteEmptyDir(File dir) {
        if (!dir.isDirectory()) {
            throw new IllegalArgumentException("Can delete only folders");
        }
        if (dir.listFiles()==null || Objects.requireNonNull(dir.listFiles()).length>0) {
            return false;
        }
        return dir.delete();
    }

    public static boolean sameContent(Settings settings, File sourceFile, File destFile) throws IOException {
        Optional<FileInfo> source = FileInfo.getInstance(settings, sourceFile);
        Optional<FileInfo> dest = FileInfo.getInstance(settings, destFile);
        if(source.isPresent() && dest.isPresent() && source.get().potentiallySameFile(dest.get())) {
            switch (settings.getCompareMode()) {
                case CONTENT:
                    // they seem the same file. Checking for content:
                    return FileUtils.contentEquals(sourceFile, destFile);
                case DATE:
                    // they seem the same file. Checking for last modified date:
                    return source.get().getLastModifiedDate().equals(dest.get().getLastModifiedDate());
                case CHECKSUM:
                default:
                    // they seem the same file. Checking for checksum
                    long chkSource = FileUtils.checksumCRC32(sourceFile);
                    long chkDest = FileUtils.checksumCRC32(destFile);
                    return chkSource == chkDest;
            }
        } else {
            return false;
        }
    }
}
