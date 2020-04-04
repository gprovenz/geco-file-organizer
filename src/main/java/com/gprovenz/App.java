package com.gprovenz;

import com.gprovenz.photoor.reader.FileInfo;
import com.gprovenz.photoor.reader.PathBuilder;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configuration;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

/**
 * Hello world!
 *
 */
public class App 
{
    private static Logger logger = LogManager.getLogger();

    private static int moved = 0;

    public static void main(String[] args ) throws IOException {


        //String sourcePath = "C:\\Users\\Gas\\Documents\\Dati\\Foto";

        String sourcePath = "C:\\Users\\Gas\\Pictures";

        String destinationPath = "C:\\Users\\Gas\\Pictures";

        File testDir = new File (destinationPath);
        logger.info("Searching files...");

        Files.walk(Paths.get(sourcePath))
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .filter(f->f.isFile())
                .forEach(e->moveFile(e, testDir));

        logger.info("Removing empty folders...");

        // remove empty dirs
        Files.walk(Paths.get(sourcePath))
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .filter(f->f.isDirectory())
                .forEach(e->deleteEmptyDir(e));


        logger.info("Moved {} files successfully", moved);
    }

    private static boolean moveFile(File sourceFile, File destinationPath) {
        FileInfo fileInfo;
        try {
            fileInfo = FileInfo.getInstance(sourceFile);
        } catch (IOException e) {
            logger.error("Cannot read file {}", sourceFile.getAbsolutePath());
            return false;
        }
        File destFile = PathBuilder.buildDestPath(destinationPath, fileInfo);

        if (sourceFile.equals(destFile)) {
            logger.info("Skipping moving same file {} ->  {}", sourceFile.getAbsolutePath(), destFile.getAbsolutePath());
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
            try {
                if (sameContent(sourceFile, destFile)) {
                    logger.info("File {} already exists in path {}", sourceFile.getName(), destFile.getAbsolutePath());
                    sourceFile.delete();
                } else {
                    logger.warn("File {} already exists in path {}, but it has different content", sourceFile.getName(), destFile.getAbsolutePath());
                }
            } catch (IOException e) {
                logger.error("Cannot delete source file {}", sourceFile.getAbsolutePath());
            }
        }


        return false;
    }

    private static boolean sameContent(File sourceFile, File destFile) throws IOException {
        FileInfo source = FileInfo.getInstance(sourceFile);
        FileInfo dest = FileInfo.getInstance(destFile);
        if(source.equals(dest)) {
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
