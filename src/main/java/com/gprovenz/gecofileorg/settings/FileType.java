
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

    public String getFileType() {
        return fileType;
    }

    public boolean isReadExifMetadata() {
        return readExifMetadata;
    }

    public boolean isIgnore() {
        return ignore;
    }
}
