package com.gprovenz.gecofileorg.utils;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/*
    Class for printing a text representation of a directory tree.
    Code has been inspired from https://stackoverflow.com/questions/10655085/print-directory-tree
*/
public class DirectoryTree {
    private DirectoryTree() {}

    public static String getTree(File root) {
        StringBuilder sb = new StringBuilder();
        printDirectoryTree(root, 2, sb);
        return sb.toString();
    }

    private static void printDirectoryTree(File folder, int indent,
                                           StringBuilder sb) {
        if (!folder.isDirectory()) {
            throw new IllegalArgumentException("folder is not a Directory");
        }
        sb.append(getIndentString(indent));
        sb.append("+--");
        sb.append(folder.getName());
        sb.append("\n");
        if (folder.listFiles()!=null) {
            List<File> files = Arrays.asList(Objects.requireNonNull(folder.listFiles()));
            files.sort(Comparator.comparing(File::getName));
            for (File file : files) {
                if (file.isDirectory()) {
                    printDirectoryTree(file, indent + 1, sb);
                } else {
                    printFile(file, indent + 1, sb);
                }
            }
        }
    }

    private static void printFile(File file, int indent, StringBuilder sb) {
        sb.append(getIndentString(indent));
        sb.append("+--");
        sb.append(file.getName());
        sb.append("\n");
    }

    private static String getIndentString(int indent) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indent; i++) {
            sb.append("|  ");
        }
        return sb.toString();
    }
}
