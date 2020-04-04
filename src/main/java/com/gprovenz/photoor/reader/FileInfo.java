package com.gprovenz.photoor.reader;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
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

public class FileInfo {
    private static Logger logger = LogManager.getLogger();

    private static final String[] PICTURE_EXTS = new String[] {
            "jpg", "jpeg", "raw", "cr2", "gif", "bmp", "psd", "tiff", "tif", "png"};
    private static final String[] VIDEO_EXTS = new String[] {
            "mpg", "mpeg", "mkv", "mp4", "avi", "vid", "mov"};

    public enum MediaType { PICTURE, VIDEO, OTHER }
    private String fileName;
    private long size;
    private Date creationDate;
    private Date lastModifiedDate;
    private MediaType mediaType;
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

    public static FileInfo getInstance(File file) throws IOException {
        if (!file.isFile()) {
            throw new IllegalArgumentException("Invalid file " + file.getAbsolutePath());
        }
        if (!file.exists()) {
            throw new FileNotFoundException("File not found: " + file.getAbsolutePath());
        }
        FileInfo fi = new FileInfo();
        BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
        fi.size = attr.size();
        fi.fileName = file.getName();
        fi.mediaType = detectMediaType(file);
        fi.creationDate = new Date(attr.creationTime().toMillis());
        fi.lastModifiedDate = new Date(attr.lastModifiedTime().toMillis());

        fi.creationDate = getMinValidDate(fi.creationDate, fi.lastModifiedDate);

        if (fi.mediaType == MediaType.PICTURE) {
            try {
                Date exifDate = readCreationDateFromExifMetadata(file);
                if (exifDate == null) {
                    logger.debug("EXIF metadata not fond for file '{}'", file.getAbsolutePath());
                } else {
                    fi.creationDate = getMinValidDate(fi.creationDate, exifDate);
                    fi.hasExif = true;
                }
            } catch (ImageProcessingException e) {
                logger.warn("Error reading EXIF metadata from file {}", file.getAbsolutePath());
            }
        }

        return fi;
    }

    private static MediaType detectMediaType(File file) {
        final String fileName = file.getName().toLowerCase();
        for (String ext:PICTURE_EXTS) {
            if (fileName.endsWith("." + ext)) {
                return MediaType.PICTURE;
            }
        }
        for (String ext:VIDEO_EXTS) {
            if (fileName.endsWith("." + ext)) {
                return MediaType.VIDEO;
            }
        }
        return MediaType.OTHER;
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

    public MediaType getMediaType() {
        return mediaType;
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
                lastModifiedDate.equals(fileInfo.lastModifiedDate) &&
                mediaType == fileInfo.mediaType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileName, size, creationDate, lastModifiedDate, mediaType, hasExif);
    }

    @Override
    public String toString() {
        return "FileInfo {" +
                "\n fileName =          " + fileName +
                "\n size =              " + size +
                "\n creationDate =      " + creationDate +
                "\n lastModifiedDate =  " + lastModifiedDate +
                "\n mediaType =         " + mediaType +
                "\n EXIF metadata =     " + hasExif +
                "\n}";
    }
}
