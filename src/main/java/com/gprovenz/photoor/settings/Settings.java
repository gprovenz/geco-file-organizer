
package com.gprovenz.photoor.settings;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Settings {
    @JsonProperty("destination_path")
    private String destinationPath;

    @JsonProperty("destination_path_structure")
    private String destinationPathStructure;

    @JsonProperty("file_types")
    private List<FileType> fileTypes;

    private Options.Operation operation;

    @JsonProperty("remove_duplicates")
    private String removeDuplicates;

    @JsonProperty("remove_empty_folders")
    private String removeEmptyFolders;

    @JsonProperty("source_path")
    private String sourcePath;

    @JsonProperty("on_existing_file_action")
    private Options.OnExistingFileAction onExistingFileAction;

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

    public String getRemoveDuplicates() {
        return removeDuplicates;
    }

    public void setRemoveDuplicates(String removeDuplicates) {
        this.removeDuplicates = removeDuplicates;
    }

    public String getRemoveEmptyFolders() {
        return removeEmptyFolders;
    }

    public void setRemoveEmptyFolders(String removeEmptyFolders) {
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

    @Override
    public String toString() {
        return "Settings{" +
                "destinationPath='" + destinationPath + '\'' +
                ", destinationPathStructure='" + destinationPathStructure + '\'' +
                ", fileTypes=" + fileTypes +
                ", operation='" + operation + '\'' +
                ", removeDuplicates='" + removeDuplicates + '\'' +
                ", removeEmptyFolders='" + removeEmptyFolders + '\'' +
                ", sourcePath='" + sourcePath + '\'' +
                ", on_existing_file_action=" + onExistingFileAction +
                '}';
    }
}
