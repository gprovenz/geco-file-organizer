
package com.gprovenz.photoor.settings;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class FileType {

    private List<String> extensions;

    @JsonProperty("file_type")
    private String fileType;

    @JsonProperty("read_exif_metadata")
    private Boolean readExifMetadata;

    private boolean ignore;

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

    public Boolean getReadExifMetadata() {
        return readExifMetadata;
    }

    public void setReadExifMetadata(Boolean readExifMetadata) {
        this.readExifMetadata = readExifMetadata;
    }

    @Override
    public String toString() {
        return "FileType{" +
                "extensions=" + extensions +
                ", fileType='" + fileType + '\'' +
                ", useExifMetadata=" + readExifMetadata +
                '}';
    }

    public boolean isIgnore() {
        return ignore;
    }

    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
    }
}
