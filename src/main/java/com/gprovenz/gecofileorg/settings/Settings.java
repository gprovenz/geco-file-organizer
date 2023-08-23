
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

    @JsonProperty("compare_mode")
    private Options.CompareMode compareMode;

    @JsonProperty("locale")
    private Locale locale;

    public String getDestinationPath() {
        return destinationPath;
    }

    public void setDestinationPath(String destinationPath) {
        this.destinationPath = destinationPath;
    }

    public String getDestinationPathStructure() {
        return destinationPathStructure;
    }

    public void setDestinationPathStructure(String destinationPathStructure) {
        this.destinationPathStructure = destinationPathStructure;
    }

    public List<FileType> getFileTypes() {
        return fileTypes;
    }

    public void setFileTypes(List<FileType> fileTypes) {
        this.fileTypes = fileTypes;
    }

    public Options.Operation getOperation() {
        return operation;
    }

    public void setOperation(Options.Operation operation) {
        this.operation = operation;
    }

    public boolean isRemoveDuplicates() {
        return removeDuplicates;
    }

    public void setRemoveDuplicates(boolean removeDuplicates) {
        this.removeDuplicates = removeDuplicates;
    }

    public boolean isRemoveEmptyFolders() {
        return removeEmptyFolders;
    }

    public void setRemoveEmptyFolders(boolean removeEmptyFolders) {
        this.removeEmptyFolders = removeEmptyFolders;
    }

    public String getSourcePath() {
        return sourcePath;
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    public Options.OnExistingFileAction getOnExistingFileAction() {
        return onExistingFileAction;
    }

    public void setOnExistingFileAction(Options.OnExistingFileAction onExistingFileAction) {
        this.onExistingFileAction = onExistingFileAction;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public Options.CompareMode getCompareMode() {
        return compareMode==null ? Options.CompareMode.CHECKSUM : compareMode;
    }

    public void setCompareMode(Options.CompareMode compareMode) {
        this.compareMode = compareMode;
    }
}
