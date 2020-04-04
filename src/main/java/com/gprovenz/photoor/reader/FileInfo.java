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
import java.util.Date;
import java.util.Objects;

public class FileInfo {
    private static Logger logger = LogManager.getLogger();

    private static final String[] PICTURE_EXTS = new String[] {".jpg", ".jpeg"};
    private static final String[] VIDEO_EXTS = new String[] {".mpg", ".mpeg", ".mkv", ".mp4", ".avi", ".vid"};

    public enum MediaType { PICTURE, VIDEO, OTHER }
    private String fileName;
    private long size;
    private Date creationDate;
    private MediaType mediaType;

    private FileInfo() { }

    public static FileInfo getInstance(File file) throws IOException {
        if (!file.isFile()) {
            throw new IllegalArgumentException("Invalid file " + file.getAbsolutePath());
        }
        if (!file.exists()) {
            throw new FileNotFoundException("File not found: " + file.getAbsolutePath());
        }
        FileInfo fi = new FileInfo();

        fi.fileName = file.getName();
        fi.mediaType = detectMediaType(file);

        if (fi.mediaType == MediaType.PICTURE) {
            try {
                fi.creationDate = readCreationDateFromExifMetadata(file);
                if (fi.creationDate == null) {
                    logger.info("EXIF metadata not fond for file '{}'", file.getAbsolutePath());
                }
            } catch (ImageProcessingException e) {
                logger.warn("Error reading EXIF metadata from file " + file.getAbsolutePath());
            }
        }

        BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
        fi.size = attr.size();

        if (fi.creationDate==null) {
            fi.creationDate = new Date(attr.creationTime().toMillis());
        }
        return fi;
    }

    private static MediaType detectMediaType(File file) {
        final String fileName = file.getName().toLowerCase();
        for (String ext:PICTURE_EXTS) {
            if (fileName.endsWith(ext)) {
                return MediaType.PICTURE;
            }
        }
        for (String ext:VIDEO_EXTS) {
            if (fileName.endsWith(ext)) {
                return MediaType.PICTURE;
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

    public long getSize() {
        return size;
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
                fileName.equals(fileInfo.fileName) &&
                creationDate.equals(fileInfo.creationDate) &&
                mediaType == fileInfo.mediaType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileName, size, creationDate, mediaType);
    }

    @Override
    public String toString() {
        return "FileInfo {" +
                "\n fileName = '    " + fileName + '\'' +
                "\n size =          " + size +
                "\n creationDate =  " + creationDate +
                "\n mediaType =     " + mediaType +
                '}';
    }
}
