package com.gprovenz.gecofileorg.reader;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.gprovenz.gecofileorg.settings.FileType;
import com.gprovenz.gecofileorg.settings.Settings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

public class FileInfo {
    private static Logger logger = LogManager.getLogger();

    private String fileName;
    private long size;
    private Date creationDate;
    private Date lastModifiedDate;
    private Optional<FileType> fileType;

    public Optional<FileType> getFileType() {
        return fileType;
    }

    public void setFileType(Optional<FileType> fileType) {
        this.fileType = fileType;
    }

    private boolean hasExif;

    private FileInfo() { }

    private static boolean isValidDate(Date d) {
        if (d==null) {
            return false;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        return c.get(Calendar.YEAR) > 1970;
    }

    private static Date getMinValidDate(Date d1, Date d2) {
        if (isValidDate(d1) && d1.before(d2)) {
            return d1;
        }
        if (isValidDate(d2) && d2.before(d1)) {
            return d2;
        }
        if (isValidDate(d1)) {
            return d1;
        }
        if (isValidDate(d2)) {
            return d2;
        }
        return new Date();
    }

    public static Optional<FileInfo> getInstance(Settings settings, File file) throws IOException {
        if (!file.isFile()) {
            throw new IllegalArgumentException("Invalid file " + file.getAbsolutePath());
        }
        if (!file.exists()) {
            throw new FileNotFoundException("File not found: " + file.getAbsolutePath());
        }
        FileInfo fileInfo = new FileInfo();
        fileInfo.fileType = getFileType(settings, file);
        if (!fileInfo.fileType.isPresent()) {
            return Optional.empty();
        }

        BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
        fileInfo.size = attr.size();
        fileInfo.fileName = file.getName();
        fileInfo.creationDate = new Date(attr.creationTime().toMillis());
        fileInfo.lastModifiedDate = new Date(attr.lastModifiedTime().toMillis());
        fileInfo.creationDate = getMinValidDate(fileInfo.creationDate, fileInfo.lastModifiedDate);

        if (fileInfo.fileType.get().isReadExifMetadata()) {
            try {
                Date exifDate = readCreationDateFromExifMetadata(file);
                if (exifDate == null) {
                    logger.debug("EXIF metadata not fond for file '{}'", file.getAbsolutePath());
                } else {
                    fileInfo.creationDate = getMinValidDate(fileInfo.creationDate, exifDate);
                    fileInfo.hasExif = true;
                }
            } catch (ImageProcessingException e) {
                logger.warn("Error reading EXIF metadata from file {}", file.getAbsolutePath());
            }
        }

        return Optional.of(fileInfo);
    }

    private static Optional<FileType> getFileType(Settings settings, File file) {
        final String fileName = file.getName().toLowerCase();
        for (FileType fileType:settings.getFileTypes()) {
            for (String ext:fileType.getExtensions()) {
                if (fileName.endsWith("." + ext)) {
                    return Optional.of(fileType);
                }
            }
        }
        return Optional.empty();
    }

    private static Date readCreationDateFromExifMetadata(File file) throws ImageProcessingException, IOException {
        final Metadata metadata = ImageMetadataReader.readMetadata(file);
        final Directory directory = metadata.getDirectory(ExifSubIFDDirectory.class);
        if (directory != null) {
            return directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
        }
        return null;
    }

    public String getFileName() {
        return fileName;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FileInfo)) return false;
        FileInfo fileInfo = (FileInfo) o;
        return size == fileInfo.size &&
                hasExif == fileInfo.hasExif &&
                fileName.equals(fileInfo.fileName) &&
                creationDate.equals(fileInfo.creationDate) &&
                lastModifiedDate.equals(fileInfo.lastModifiedDate);
    }

    public boolean potentiallySameFile(FileInfo fileInfo) {
        return size == fileInfo.size &&
                hasExif == fileInfo.hasExif &&
                fileName.equalsIgnoreCase(fileInfo.fileName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileName, size, creationDate, lastModifiedDate, hasExif);
    }

    @Override
    public String toString() {
        return "FileInfo {" +
                "\n fileName =          " + fileName +
                "\n size =              " + size +
                "\n creationDate =      " + creationDate +
                "\n lastModifiedDate =  " + lastModifiedDate +
                "\n EXIF metadata =     " + hasExif +
                "\n}";
    }
}
