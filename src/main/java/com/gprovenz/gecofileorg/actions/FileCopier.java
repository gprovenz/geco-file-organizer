package com.gprovenz.gecofileorg.actions;

import com.gprovenz.gecofileorg.reader.FileInfo;
import com.gprovenz.gecofileorg.reader.FileTools;
import com.gprovenz.gecofileorg.reader.PathBuilder;
import com.gprovenz.gecofileorg.settings.Settings;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static com.gprovenz.gecofileorg.log.LogMessage.*;
import static com.gprovenz.gecofileorg.reader.FileTools.isToIgnore;

public class FileCopier {
    private final Settings settings;
    private static final Logger logger = LoggerFactory.getLogger(FileCopier.class);

    private int copied;

    public FileCopier(Settings settings) {
        this.settings = settings;
    }

    public void copyAllFiles() throws IOException {
        copied = 0;

        File outPath = new File (settings.getDestinationPath());
        logger.info("Coping files...");

        Files.walk(Paths.get(settings.getSourcePath()))
                .map(Path::toFile)
                .filter(File::isFile)
                .forEach(f -> copyFile(settings, f, outPath));

        logger.info("Copied {} files successfully", copied);
    }

    private void copyFile(Settings settings, File sourceFile, File destinationPath) {
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
        if (isToIgnore(fileInfo.get())) {
            logger.debug("Ignoring file {}", sourceFile.getAbsolutePath());
            return;
        }

        File destFile = PathBuilder.buildDestPath(settings, destinationPath, fileInfo.get());

        if (sourceFile.equals(destFile)) {
            logger.debug("Skipping copying same file {} -> {}", sourceFile.getAbsolutePath(), destFile.getAbsolutePath());
            return;
        }

        if (!destFile.exists()) {
            try {
                File destParent = new File(destFile.getParent());
                if (destParent.mkdirs()) {
                    logger.debug("Creating directory {}", destParent);
                }

                FileUtils.copyFile(sourceFile, destFile);
                logCopied(sourceFile, destFile);
            } catch (IOException e) {
                logger.error("Error copying file " + sourceFile.getAbsolutePath() + " to " + destinationPath, e);
                return;
            }
            logCopied(sourceFile, destFile);
            copied++;
        } else {

            try {
                if (FileTools.sameContent(settings, sourceFile, destFile)) {
                    logger.debug("File {} already exists in path {}", sourceFile.getName(), destFile.getAbsolutePath());
                    logIgnoredDuplicate(sourceFile, destFile);
                } else {
                    logDifferentContent(sourceFile, destFile);
                }
            } catch (IOException e) {
                logger.error("Cannot delete source file {}", sourceFile.getAbsolutePath(), e);

            }
        }
    }
}
