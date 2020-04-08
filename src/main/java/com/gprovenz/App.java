package com.gprovenz;

import com.gprovenz.photoor.reader.FileInfo;
import com.gprovenz.photoor.reader.PathBuilder;
import com.gprovenz.photoor.settings.Options;
import com.gprovenz.photoor.settings.Settings;
import com.gprovenz.photoor.settings.SettingsReader;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Hello world!
 *
 */
public class App
{
    public static final int THREADS = 4;
    private static Logger logger = LogManager.getLogger();

    private static int moved = 0;
    private static int copied = 0;

    public static void main(String[] args ) throws IOException, InterruptedException {
        execCommand(new File(args[1]));
    }

    private static void execCommand(File settingsFile) throws IOException, InterruptedException {
        Settings settings = SettingsReader.read(settingsFile);
        if (settings.getOperation()== Options.Operation.MOVE) {
            moveAllFiles(settings);
        } else if (settings.getOperation()==Options.Operation.COPY) {
            copyAllFiles(settings);
        } else {
            throw new IllegalArgumentException("Invalid operation: " + settings.getOperation());
        }
    }

    private static void moveAllFiles(Settings settings) throws IOException, InterruptedException {
        moved = 0;

       ExecutorService executor = Executors.newFixedThreadPool(THREADS);

        File outPath = new File (settings.getDestinationPath());
        logger.info("Moving files...");

        Files.walk(Paths.get(settings.getSourcePath()))
                .map(Path::toFile)
                .filter(f -> f.isFile())
                .forEach(f -> moveFile(settings, f, outPath, executor));

        executor.shutdown();

        if (settings.isRemoveEmptyFolders()) {
            removeEmptyFolders(settings.getSourcePath());
        }

        logger.info("Moved {} files successfully", moved);
    }

    private static void removeEmptyFolders(String folderPath) throws IOException {
        logger.info("Removing empty folders...");

        // remove empty dirs
        Files.walk(Paths.get(folderPath))
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .filter(f -> f.isDirectory())
                .forEach(e -> deleteEmptyDir(e));

    }

    private static void copyAllFiles(Settings settings) throws IOException, InterruptedException {
        copied = 0;

        File outPath = new File (settings.getDestinationPath());
        logger.info("Coping files...");

        Files.walk(Paths.get(settings.getSourcePath()))
                .map(Path::toFile)
                .filter(f -> f.isFile())
                .forEach(f -> copyFile(settings, f, outPath));

        logger.info("Copied {} files successfully", copied);
    }

    private static boolean moveFile(Settings settings, File sourceFile, File destinationPath, ExecutorService executor) {
        Optional<FileInfo> fileInfo;
        try {
            fileInfo = FileInfo.getInstance(settings, sourceFile);
        } catch (IOException e) {
            logger.error("Cannot read file {}", sourceFile.getAbsolutePath());
            return false;
        }
        if (isToIgnore(fileInfo))  {
            logger.debug("Ignoring file {}", sourceFile.getAbsolutePath());
            return false;
        }

        File destFile = PathBuilder.buildDestPath(settings, destinationPath, fileInfo.get());

        if (sourceFile.equals(destFile)) {
            logger.debug("Skipping moving same file {} -> {}", sourceFile.getAbsolutePath(), destFile.getAbsolutePath());
            return false;
        }

        if (!destFile.exists()) {
            new File(destFile.getParent()).mkdirs();
            try {
                FileUtils.moveFile(sourceFile, destFile);
            } catch (IOException e) {
                logger.error("Cannot move file {}", sourceFile.getAbsolutePath());
                return false;
            }
            logger.info("File {} moved to {}", sourceFile.getName(), destFile.getAbsolutePath());
            moved++;
            return true;
        } else {
            executor.submit(() -> {
                try {
                    if (sameContent(settings, sourceFile, destFile)) {
                        logger.info("File {} already exists in path {}", sourceFile.getName(), destFile.getAbsolutePath());
                        sourceFile.delete();
                    } else {
                        logger.warn("File {} already exists in path {}, but it has different content", sourceFile.getName(), destFile.getAbsolutePath());
                    }
                } catch (IOException e) {
                    logger.error("Cannot delete source file {}: {}", sourceFile.getAbsolutePath(), e.getMessage());
                }
            });
        }

        return false;
    }

    private static boolean copyFile(Settings settings, File sourceFile, File destinationPath) {
        Optional<FileInfo> fileInfo;
        try {
            fileInfo = FileInfo.getInstance(settings, sourceFile);
        } catch (IOException e) {
            logger.error("Cannot read file {}", sourceFile.getAbsolutePath());
            return false;
        }
        if (isToIgnore(fileInfo))  {
            logger.debug("Ignoring file {}", sourceFile.getAbsolutePath());
            return false;
        }

        File destFile = PathBuilder.buildDestPath(settings, destinationPath, fileInfo.get());

        if (sourceFile.equals(destFile)) {
            logger.debug("Skipping copying same file {} -> {}", sourceFile.getAbsolutePath(), destFile.getAbsolutePath());
            return false;
        }

        if (!destFile.exists()) {
            new File(destFile.getParent()).mkdirs();
            try {
                FileUtils.copyFile(sourceFile, destFile);
            } catch (IOException e) {
                logger.error("Cannot copy file {}", sourceFile.getAbsolutePath());
                return false;
            }
            logger.info("File {} copied to {}", sourceFile.getName(), destFile.getAbsolutePath());
            copied++;
            return true;
        }

        logger.info("Skipping existing file: {} already exists in path {}", sourceFile.getName(), destFile.getAbsolutePath());
        return false;
    }

    private static boolean isToIgnore(Optional<FileInfo> fileInfo) {
        return !fileInfo.isPresent() ||
                !fileInfo.get().getFileType().isPresent() ||
                fileInfo.get().getFileType().get().isIgnore();
    }

    private static boolean sameContent(Settings settings, File sourceFile, File destFile) throws IOException {
        Optional<FileInfo> source = FileInfo.getInstance(settings, sourceFile);
        Optional<FileInfo> dest = FileInfo.getInstance(settings, destFile);
        if(source.isPresent() && dest.isPresent() && source.get().potentiallySameFile(dest.get())) {
            // they seem the same file. Checking for content:
            return FileUtils.contentEquals(sourceFile, destFile);
        } else {
            return false;
        }
    }

    private static boolean deleteEmptyDir(File f) {
        if (!f.isDirectory()) {
            throw new IllegalArgumentException("Can delete only folders");
        }
        if (f.listFiles().length>0) {
            return false;
        }
        return f.delete();
    }
}
