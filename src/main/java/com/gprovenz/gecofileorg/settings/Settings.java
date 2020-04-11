
package com.gprovenz.gecofileorg.settings;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.Locale;

@Getter
@ToString
public class Settings {
    @JsonProperty("destination_path")
    private String destinationPath;

    @JsonProperty("destination_path_structure")
    private String destinationPathStructure;

    @JsonProperty("file_types")
    private List<FileType> fileTypes;

    private Options.Operation operation;

    @JsonProperty("remove_duplicates")
    private boolean removeDuplicates;

    @JsonProperty("remove_empty_folders")
    private boolean removeEmptyFolders;

    @JsonProperty("source_path")
    private String sourcePath;

    @JsonProperty("on_existing_file_action")
    private Options.OnExistingFileAction onExistingFileAction;

    @JsonProperty("locale")
    private Locale locale;


}
