
package com.gprovenz.gecofileorg.settings;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class FileType {
    private List<String> extensions;

    @JsonProperty("file_type")
    private String fileType;

    @JsonProperty("read_exif_metadata")
    private boolean readExifMetadata;

    private boolean ignore;
    private FileSize minSize;
    private FileSize maxSize;

    public List<String> getExtensions() {
        return extensions;
    }

    public void setExtensions(List<String> extensions) {
        this.extensions = extensions;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public boolean isReadExifMetadata() {
        return readExifMetadata;
    }

    public void setReadExifMetadata(boolean readExifMetadata) {
        this.readExifMetadata = readExifMetadata;
    }

    public boolean isIgnore() {
        return ignore;
    }

    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
    }

    public FileSize getMinSize() {
        return minSize;
    }

    public void setMinSize(FileSize minSize) {
        this.minSize = minSize;
    }

    public FileSize getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(FileSize maxSize) {
        this.maxSize = maxSize;
    }
}
