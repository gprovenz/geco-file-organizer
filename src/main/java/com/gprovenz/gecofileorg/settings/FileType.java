
package com.gprovenz.gecofileorg.settings;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class FileType {
    private List<String> extensions;

    @JsonProperty("file_type")
    private String fileType;

    @JsonProperty("read_exif_metadata")
    private boolean readExifMetadata;

    private boolean ignore;
}
