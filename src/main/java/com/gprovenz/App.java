package com.gprovenz;

import com.gprovenz.photoor.reader.FileInfo;
import com.gprovenz.photoor.reader.PathBuilder;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
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
        System.out.println(String.format("Searching files..."));

        Files.walk(Paths.get(sourcePath))
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .filter(f->f.isFile())
                .forEach(e->moveFile(e, testDir));

        System.out.println(String.format("Removing empty folders..."));

        // remove empty dirs
        Files.walk(Paths.get(sourcePath))
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .filter(f->f.isDirectory())
                .forEach(e->deleteEmptyDir(e));


        System.out.println(String.format("Moved %s files successfully", moved));
    }

    private static boolean moveFile(File sourceFile, File destinationPath) {
        FileInfo fileInfo;
        try {
            fileInfo = FileInfo.getInstance(sourceFile);
        } catch (IOException e) {
            logger.error("Cannot read file %s", sourceFile.getAbsolutePath());
            return false;
        }
        File dest = PathBuilder.buildDestPath(destinationPath, fileInfo);
        if (!dest.exists()) {
            new File(dest.getParent()).mkdirs();
            try {
                FileUtils.moveFile(sourceFile, dest);
            } catch (IOException e) {
                logger.error("Cannot move file %s", sourceFile.getAbsolutePath());
                return false;
            }
            System.out.println(String.format("File %s moved to %s", sourceFile.getName(), dest.getAbsolutePath()));
            moved++;
            return true;
        }

        System.out.println(String.format("File %s already exists in path %s", sourceFile.getName(), dest.getAbsolutePath()));
        return false;
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
