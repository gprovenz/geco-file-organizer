
package com.gprovenz.gecofileorg.settings;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Locale;

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

    public String getDestinationPath() {
        return destinationPath;
    }

    public String getDestinationPathStructure() {
        return destinationPathStructure;
    }

    public List<FileType> getFileTypes() {
        return fileTypes;
    }

    public Options.Operation getOperation() {
        return operation;
    }

    public boolean isRemoveDuplicates() {
        return removeDuplicates;
    }

    public boolean isRemoveEmptyFolders() {
        return removeEmptyFolders;
    }

    public String getSourcePath() {
        return sourcePath;
    }

    public Options.OnExistingFileAction getOnExistingFileAction() {
        return onExistingFileAction;
    }

    public Locale getLocale() {
        return locale;
    }
}
