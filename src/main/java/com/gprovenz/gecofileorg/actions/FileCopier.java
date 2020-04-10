package com.gprovenz.gecofileorg.actions;

import com.gprovenz.gecofileorg.reader.FileInfo;
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

import static com.gprovenz.gecofileorg.reader.FileTools.isToIgnore;

public class FileCopier {

    private final Settings settings;
    private Logger logger = LogManager.getLogger();
    private int copied;

    public FileCopier(Settings settings) {
        this.settings = settings;
    }

    public void copyAllFiles() throws IOException, InterruptedException {
        copied = 0;

        File outPath = new File (settings.getDestinationPath());
        logger.info("Coping files...");

        Files.walk(Paths.get(settings.getSourcePath()))
                .map(Path::toFile)
                .filter(f -> f.isFile())
                .forEach(f -> copyFile(settings, f, outPath));

        logger.info("Copied {} files successfully", copied);
    }

    private boolean copyFile(Settings settings, File sourceFile, File destinationPath) {
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

}
