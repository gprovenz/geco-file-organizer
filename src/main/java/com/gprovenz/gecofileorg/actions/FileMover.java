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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.gprovenz.gecofileorg.log.LogMessage.*;

public class FileMover {
    public static final int THREADS = 4;

    private final Settings settings;
    private int moved;
    private final Logger logger = LogManager.getLogger();

    public FileMover(Settings settings) {
        this.settings = settings;
    }

    public void moveAllFiles() throws IOException {
        moved = 0;

        ExecutorService executor = Executors.newFixedThreadPool(THREADS);
        List<Future<Boolean>> futures = new ArrayList<>();

        File outPath = new File (settings.getDestinationPath());
        logger.info("Moving files...");

        Files.walk(Paths.get(settings.getSourcePath()))
                .map(Path::toFile)
                .filter(File::isFile)
                .forEach(f -> moveFile(settings, f, outPath, executor, futures));

        logger.debug("Cleaning up...");
        for (Future<Boolean> f:futures) {
            try {
                f.get();
            } catch (InterruptedException | ExecutionException e) {
                logger.error ("Error while waiting for cleaning threads", e);
            }
        }

        executor.shutdown();

        if (settings.isRemoveEmptyFolders()) {
            FileTools.removeEmptyFolders(settings.getSourcePath());
        }

        logger.info("{} files moved successfully", moved);
    }

    private void moveFile(Settings settings, File sourceFile, File destinationPath, ExecutorService executor, List<Future<Boolean>> futures) {
        Optional<FileInfo> fileInfo;
        try {
            fileInfo = FileInfo.getInstance(settings, sourceFile);
            if (!fileInfo.isPresent()) {
                return;
            }
        } catch (IOException e) {
            logger.error("Cannot read file " + sourceFile.getAbsolutePath(), e);
            return;
        }
        if (FileTools.isToIgnore(fileInfo.get()))  {
            logger.debug("Ignoring file {}", sourceFile.getAbsolutePath());
            return;
        }

        File destFile = PathBuilder.buildDestPath(settings,
                destinationPath,
                fileInfo.get());

        if (sourceFile.equals(destFile)) {
            logger.debug("Skipping moving same file {} -> {}", sourceFile.getAbsolutePath(), destFile.getAbsolutePath());
            return;
        }

        if (!destFile.exists()) {
            try {
                File destParent = new File(destFile.getParent());
                if (destParent.mkdirs()) {
                    logger.debug("Creating directory {}", destParent);
                }

                FileUtils.moveFile(sourceFile, destFile);
                logMoved(sourceFile, destFile);
            } catch (IOException e) {
                logger.error("Error moving file " + sourceFile.getAbsolutePath() + " to " + destinationPath, e);
                return;
            }
            logger.debug("File {} moved to {}", sourceFile.getName(), destFile.getAbsolutePath());
            moved++;
        } else {
            futures.add(executor.submit(() -> {
                try {
                    if (FileTools.sameContent(settings, sourceFile, destFile)) {
                        logger.debug("File {} already exists in path {}", sourceFile.getName(), destFile.getAbsolutePath());
                        if (settings.isRemoveDuplicates()) {
                            final boolean delete = sourceFile.delete();
                            if (delete) {
                                logRemovedDuplicate(sourceFile, destFile);
                                return true;
                            } else {
                                logIgnoredDuplicate(sourceFile, destFile);
                            }
                        } else {
                            logIgnoredDuplicate(sourceFile, destFile);
                        }
                    } else {
                        logDifferentContent(sourceFile, destFile);
                    }
                } catch (IOException e) {
                    logger.error("Cannot delete source file {}", sourceFile.getAbsolutePath(), e);

                }
                return false;
            }));
        }

    }
}
