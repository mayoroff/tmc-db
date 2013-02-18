package org.tmcdb.utils;

import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * @author Pavel Talanov
 */
public final class DirectoryUtils {

    private DirectoryUtils() {
    }

    @NotNull
    public static File ensureSubDirectoryExists(@NotNull File parent, @NotNull String child) {
        File directory = new File(parent, child);
        if (!directory.exists()) {
            //noinspection ResultOfMethodCallIgnored
            directory.mkdir();
        }
        if (directory.isFile()) {
            throw new IllegalStateException("Invalid location for initializing database");
        }
        return directory;
    }

    public static void checkIsDirectory(@NotNull File file) {
        if (!file.isDirectory()) {
            throw new IllegalArgumentException(file.getAbsolutePath() + " is not a directory");
        }
    }
}
