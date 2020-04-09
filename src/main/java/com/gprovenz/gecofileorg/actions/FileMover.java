package com.gprovenz.gecofileorg.actions;

import com.gprovenz.gecofileorg.reader.FileInfo;
import com.gprovenz.gecofileorg.reader.FileTools;
import com.gprovenz.gecofileorg.reader.PathBuilder;
import com.gprovenz.gecofileorg.settings.Settings;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileMover {
    public static final int THREADS = 4;

    private final Settings settings;
    private int moved;
    private Logger logger = LogManager.getLogger();

    public FileMover(Settings settings) {
        this.settings = settings;
    }

    public void moveAllFiles() throws IOException {
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
            FileTools.removeEmptyFolders(settings.getSourcePath());
        }

        logger.info("Moved {} files successfully", moved);
    }

    private boolean moveFile(Settings settings, File sourceFile, File destinationPath, ExecutorService executor) {
        Optional<FileInfo> fileInfo;
        try {
            fileInfo = FileInfo.getInstance(settings, sourceFile);
        } catch (IOException e) {
            logger.error("Cannot read file {}", sourceFile.getAbsolutePath());
            return false;
        }
        if (FileTools.isToIgnore(fileInfo))  {
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
                    if (FileTools.sameContent(settings, sourceFile, destFile)) {
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
}
