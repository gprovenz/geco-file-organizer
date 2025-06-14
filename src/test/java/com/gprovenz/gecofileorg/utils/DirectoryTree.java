package com.gprovenz.gecofileorg.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.*;
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
        printDirectoryTree(root, 0, sb);
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
                }
            }
            for (File file : files) {
                if (file.isFile()) {
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

    /**
     * Denies write access to the "Users" group for the given folder.
     * @param folderPath The path to the folder to protect.
     * @throws IOException If an I/O error occurs or ACLs are unsupported.
     */
    public static void denyWriteAccess(Path folderPath) throws IOException {
        // Ensure the folder exists
        if (!Files.exists(folderPath) || !Files.isDirectory(folderPath)) {
            throw new IllegalArgumentException("Path must be an existing directory: " + folderPath);
        }

        // Get ACL view
        AclFileAttributeView aclView = Files.getFileAttributeView(folderPath, AclFileAttributeView.class);
        if (aclView == null) {
            throw new UnsupportedOperationException("ACL view not supported on this file system.");
        }

        // Lookup "Users" group (can change to a specific user)
        UserPrincipalLookupService lookupService = folderPath.getFileSystem().getUserPrincipalLookupService();
        UserPrincipal usersGroup = lookupService.lookupPrincipalByName("Users");

        // Create a DENY ACL entry for write permissions
        AclEntry denyWriteEntry = AclEntry.newBuilder()
                .setType(AclEntryType.DENY)
                .setPrincipal(usersGroup)
                .setPermissions(
                        AclEntryPermission.WRITE_DATA,
                        AclEntryPermission.APPEND_DATA,
                        AclEntryPermission.WRITE_ATTRIBUTES,
                        AclEntryPermission.WRITE_ACL,
                        AclEntryPermission.WRITE_OWNER
                )
                .build();

        // Get and update the ACL list
        List<AclEntry> aclList = aclView.getAcl();
        aclList.add(0, denyWriteEntry); // Add at beginning to ensure DENY precedence
        aclView.setAcl(aclList);

        System.out.println("Write access denied for 'Users' to folder: " + folderPath);
    }
}
